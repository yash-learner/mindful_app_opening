package com.mindful.appopening.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.appcompat.app.AppCompatActivity
import com.mindful.appopening.R
import com.mindful.appopening.databinding.ActivityMainBinding
import com.mindful.appopening.ui.appselect.AppSelectActivity
import com.mindful.appopening.ui.onboarding.OnboardingActivity
import com.mindful.appopening.ui.todo.TodoActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = getString(R.string.app_name)
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        updatePermissionStatus()
    }

    private fun setupClickListeners() {
        binding.btnTodos.setOnClickListener {
            startActivity(Intent(this, TodoActivity::class.java))
        }
        binding.btnSelectApps.setOnClickListener {
            startActivity(Intent(this, AppSelectActivity::class.java))
        }
        binding.btnPermissions.setOnClickListener {
            startActivity(Intent(this, OnboardingActivity::class.java))
        }
    }

    private fun updatePermissionStatus() {
        val canDrawOverlays = Settings.canDrawOverlays(this)
        val accessibilityEnabled = isAccessibilityServiceEnabled()
        val allGranted = canDrawOverlays && accessibilityEnabled

        if (allGranted) {
            binding.tvPermissionStatus.text = getString(R.string.status_permissions_ok)
            binding.tvPermissionStatus.setTextColor(getColor(R.color.success_green))
            binding.cardPermissionStatus.strokeColor = getColor(R.color.success_green)
        } else {
            binding.tvPermissionStatus.text = getString(R.string.status_permissions_needed)
            binding.tvPermissionStatus.setTextColor(getColor(R.color.warning_orange))
            binding.cardPermissionStatus.strokeColor = getColor(R.color.warning_orange)
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val am = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        if (!am.isEnabled) return false
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabledServices.contains(packageName)
    }
}
