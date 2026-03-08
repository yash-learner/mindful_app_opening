package com.mindful.appopening.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single todo item.
 * Todos are simple text tasks - no recurrence, no due dates (MVP).
 */
@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
