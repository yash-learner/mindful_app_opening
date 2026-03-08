package com.mindful.appopening.ui.appselect

data class InstalledApp(
    val packageName: String,
    val appName: String,
    val isSelected: Boolean = false
)
