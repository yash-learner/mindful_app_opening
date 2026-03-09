package com.mindful.appopening.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityEvent
import com.mindful.appopening.MindfulApplication
import com.mindful.appopening.data.repository.AppRepository
import com.mindful.appopening.data.repository.TodoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Accessibility Service that monitors app launches.
 *
 * Technical approach:
 * - Listens for TYPE_WINDOW_STATE_CHANGED events, which fire when a new Activity
 *   window comes to the foreground.
 * - When a selected app is detected, the OverlayManager shows a full-screen overlay.
 * - Session tracking (lastTriggeredPackage) prevents the overlay from appearing
 *   repeatedly while the same app is in use.
 *
 * The user must manually enable this service in:
 *   Settings → Accessibility → Mindful
 */
class AppMonitorAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var overlayManager: OverlayManager? = null

    private lateinit var appRepository: AppRepository
    private lateinit var todoRepository: TodoRepository

    /**
     * Tracks the package name for which the overlay was last shown.
     * Prevents showing the overlay repeatedly for the same app session.
     * Cleared when the user navigates to a different (non-selected) app.
     */
    private var lastTriggeredPackage: String? = null

    companion object {
        // Singleton reference so other components can call resetSession()
        var instance: AppMonitorAccessibilityService? = null
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this

        val app = application as MindfulApplication
        appRepository = AppRepository(app.database.selectedAppDao())
        todoRepository = TodoRepository(app.database.todoDao())
        overlayManager = OverlayManager(this)

        // Configure the service programmatically in addition to the XML config
        serviceInfo = serviceInfo?.also { info ->
            info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            info.notificationTimeout = 100
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return

        // Skip system packages, launchers, and our own app to avoid recursion
        if (shouldSkipPackage(packageName)) return

        // If overlay is already visible, don't process events
        if (overlayManager?.isShowing() == true) return

        serviceScope.launch {
            try {
                val isSelected = appRepository.isAppSelected(packageName)

                if (isSelected) {
                    // Only show overlay once per app session (until user leaves the app)
                    if (packageName != lastTriggeredPackage) {
                        lastTriggeredPackage = packageName
                        val todos = todoRepository.getActiveTodos()
                        withContext(Dispatchers.Main) {
                            overlayManager?.showOverlay(todos)
                        }
                    }
                } else {
                    // User navigated to a different app — reset so overlay
                    // shows again next time the selected app is opened
                    if (packageName != lastTriggeredPackage) {
                        lastTriggeredPackage = null
                    }
                }
            } catch (e: Exception) {
                // Silently handle exceptions to avoid crashing the service
            }
        }
    }

    override fun onInterrupt() {
        overlayManager?.hideOverlay()
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        overlayManager?.hideOverlay()
        serviceScope.cancel()
    }

    /** Called by OverlayManager when the user dismisses the overlay */
    fun resetSession() {
        lastTriggeredPackage = null
    }

    private fun shouldSkipPackage(packageName: String): Boolean {
        return packageName == this.packageName ||
            packageName == "com.android.systemui" ||
            packageName == "android" ||
            packageName == "com.android.settings" ||
            packageName.startsWith("com.android.launcher") ||
            packageName.startsWith("com.google.android.apps.nexuslauncher") ||
            packageName.startsWith("com.miui.home") ||
            packageName.startsWith("com.sec.android.app.launcher") ||
            packageName.startsWith("com.samsung.android.app.launcher")
    }
}
