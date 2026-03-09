package com.mindful.appopening.data.repository

import androidx.lifecycle.LiveData
import com.mindful.appopening.data.database.TodoDao
import com.mindful.appopening.data.model.Todo

class TodoRepository(private val todoDao: TodoDao) {

    val allTodos: LiveData<List<Todo>> = todoDao.getAllTodos()

    suspend fun getActiveTodos(): List<Todo> = todoDao.getActiveTodos()

    suspend fun insert(todo: Todo) = todoDao.insert(todo)

    suspend fun update(todo: Todo) = todoDao.update(todo)

    suspend fun delete(todo: Todo) = todoDao.delete(todo)
}
