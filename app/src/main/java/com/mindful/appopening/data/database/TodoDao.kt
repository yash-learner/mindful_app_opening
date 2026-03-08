package com.mindful.appopening.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mindful.appopening.data.model.Todo

@Dao
interface TodoDao {

    @Query("SELECT * FROM todos ORDER BY isCompleted ASC, createdAt DESC")
    fun getAllTodos(): LiveData<List<Todo>>

    @Query("SELECT * FROM todos WHERE isCompleted = 0 ORDER BY createdAt DESC")
    suspend fun getActiveTodos(): List<Todo>

    @Insert
    suspend fun insert(todo: Todo)

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)
}
