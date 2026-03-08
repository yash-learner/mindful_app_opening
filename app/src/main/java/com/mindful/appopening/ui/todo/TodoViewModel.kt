package com.mindful.appopening.ui.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mindful.appopening.data.model.Todo
import com.mindful.appopening.data.repository.TodoRepository
import kotlinx.coroutines.launch

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {

    val allTodos: LiveData<List<Todo>> = repository.allTodos

    fun addTodo(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.insert(Todo(text = text.trim()))
        }
    }

    fun toggleTodo(todo: Todo) {
        viewModelScope.launch {
            repository.update(todo.copy(isCompleted = !todo.isCompleted))
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            repository.delete(todo)
        }
    }

    class Factory(private val repository: TodoRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TodoViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
