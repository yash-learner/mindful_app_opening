package com.mindful.appopening.ui.todo

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindful.appopening.MindfulApplication
import com.mindful.appopening.R
import com.mindful.appopening.databinding.ActivityTodoBinding
import com.mindful.appopening.data.repository.TodoRepository

class TodoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTodoBinding

    private val viewModel: TodoViewModel by viewModels {
        TodoViewModel.Factory(
            TodoRepository((application as MindfulApplication).database.todoDao())
        )
    }

    private lateinit var adapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.title_todos)

        setupRecyclerView()
        setupObservers()
        setupInput()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupRecyclerView() {
        adapter = TodoAdapter(
            onToggle = { todo -> viewModel.toggleTodo(todo) },
            onDelete = { todo -> viewModel.deleteTodo(todo) }
        )
        binding.rvTodos.layoutManager = LinearLayoutManager(this)
        binding.rvTodos.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.allTodos.observe(this) { todos ->
            adapter.submitList(todos)
            binding.tvEmpty.visibility = if (todos.isEmpty()) View.VISIBLE else View.GONE
            binding.rvTodos.visibility = if (todos.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun setupInput() {
        binding.btnAddTodo.setOnClickListener { addTodo() }

        binding.etNewTodo.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addTodo()
                true
            } else {
                false
            }
        }
    }

    private fun addTodo() {
        val text = binding.etNewTodo.text?.toString()?.trim()
        if (!text.isNullOrBlank()) {
            viewModel.addTodo(text)
            binding.etNewTodo.setText("")
        }
    }
}
