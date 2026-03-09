package com.mindful.appopening.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an app selected by the user to trigger the todo overlay.
 */
@Entity(tableName = "selected_apps")
data class SelectedApp(
    @PrimaryKey
    val packageName: String,
    val appName: String
)
