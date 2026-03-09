package com.mindful.appopening.ui.onboarding

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mindful.appopening.R
import com.mindful.appopening.databinding.ActivityOnboardingBinding

/**
 * Guides the user through the two permission setup steps:
 * 1. Draw Over Other Apps (SYSTEM_ALERT_WINDOW)
 * 2. Enable the Accessibility Service
 */
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.title_permissions)
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        updatePermissionUI()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupClickListeners() {
        // Opens the system dialog to grant SYSTEM_ALERT_WINDOW
        binding.btnGrantOverlay.setOnClickListener {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }

        // Opens Android Accessibility Settings so user can enable our service
        binding.btnGrantAccessibility.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        binding.btnDone.setOnClickListener {
            finish()
        }
    }

    private fun updatePermissionUI() {
        val canDrawOverlays = Settings.canDrawOverlays(this)
        val accessibilityEnabled = isAccessibilityServiceEnabled()

        // Overlay permission row
        updatePermissionRow(
            statusIcon = binding.ivOverlayStatus,
            grantButton = binding.btnGrantOverlay,
            isGranted = canDrawOverlays
        )

        // Accessibility permission row
        updatePermissionRow(
            statusIcon = binding.ivAccessibilityStatus,
            grantButton = binding.btnGrantAccessibility,
            isGranted = accessibilityEnabled
        )

        // Show Done button only when both are granted
        val allGranted = canDrawOverlays && accessibilityEnabled
        binding.btnDone.visibility = if (allGranted) View.VISIBLE else View.GONE
        binding.tvAllDone.visibility = if (allGranted) View.VISIBLE else View.GONE
    }

    private fun updatePermissionRow(
        statusIcon: android.widget.ImageView,
        grantButton: com.google.android.material.button.MaterialButton,
        isGranted: Boolean
    ) {
        if (isGranted) {
            statusIcon.setImageResource(R.drawable.ic_check_circle)
            statusIcon.setColorFilter(getColor(R.color.success_green))
            grantButton.text = getString(R.string.granted)
            grantButton.isEnabled = false
        } else {
            statusIcon.setImageResource(R.drawable.ic_pending_circle)
            statusIcon.setColorFilter(getColor(R.color.warning_orange))
            grantButton.text = getString(R.string.grant)
            grantButton.isEnabled = true
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabledServices.contains(packageName)
    }
}
