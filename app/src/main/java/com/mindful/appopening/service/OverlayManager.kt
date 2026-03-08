package com.mindful.appopening.service

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mindful.appopening.R
import com.mindful.appopening.data.model.Todo

/**
 * Manages showing and hiding the todo overlay above other apps.
 *
 * Uses WindowManager with TYPE_APPLICATION_OVERLAY (requires SYSTEM_ALERT_WINDOW permission).
 * The overlay is a full-screen view added directly to the window manager,
 * independent of any Activity lifecycle.
 */
class OverlayManager(private val context: Context) {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null

    fun showOverlay(todos: List<Todo>) {
        // Guard: don't show if permission not granted or already showing
        if (!Settings.canDrawOverlays(context)) return
        if (overlayView != null) return

        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        overlayView = LayoutInflater.from(context).inflate(R.layout.overlay_layout, null)

        setupOverlayContent(todos)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }

        try {
            windowManager?.addView(overlayView, params)
        } catch (e: Exception) {
            overlayView = null
        }
    }

    private fun setupOverlayContent(todos: List<Todo>) {
        val view = overlayView ?: return

        // Todo list
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvOverlayTodos)
        val emptyText = view.findViewById<TextView>(R.id.tvOverlayEmpty)

        if (todos.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyText.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyText.visibility = View.GONE
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = OverlayTodoAdapter(todos)
        }

        // "Continue to App" — dismiss overlay, user stays in selected app
        view.findViewById<Button>(R.id.btnContinue).setOnClickListener {
            hideOverlay()
            // Don't reset session — overlay won't show again until user leaves and returns
        }

        // "Go Home" — dismiss overlay and navigate to home screen
        view.findViewById<Button>(R.id.btnGoHome).setOnClickListener {
            hideOverlay()
            // Reset session so overlay shows again if user comes back to this app
            AppMonitorAccessibilityService.instance?.resetSession()
            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(homeIntent)
        }
    }

    fun hideOverlay() {
        try {
            overlayView?.let { windowManager?.removeView(it) }
        } catch (e: Exception) {
            // View may already be detached; safe to ignore
        } finally {
            overlayView = null
        }
    }

    fun isShowing(): Boolean = overlayView != null
}
