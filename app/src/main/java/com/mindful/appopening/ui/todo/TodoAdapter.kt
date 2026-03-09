package com.mindful.appopening.ui.todo

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mindful.appopening.data.model.Todo
import com.mindful.appopening.databinding.ItemTodoBinding

class TodoAdapter(
    private val onToggle: (Todo) -> Unit,
    private val onDelete: (Todo) -> Unit
) : ListAdapter<Todo, TodoAdapter.TodoViewHolder>(DiffCallback) {

    inner class TodoViewHolder(private val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(todo: Todo) {
            binding.tvTodoText.text = todo.text

            if (todo.isCompleted) {
                binding.tvTodoText.paintFlags =
                    binding.tvTodoText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvTodoText.alpha = 0.5f
            } else {
                binding.tvTodoText.paintFlags =
                    binding.tvTodoText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.tvTodoText.alpha = 1.0f
            }

            // Avoid triggering listener during rebind
            binding.cbTodo.setOnCheckedChangeListener(null)
            binding.cbTodo.isChecked = todo.isCompleted
            binding.cbTodo.setOnCheckedChangeListener { _, _ -> onToggle(todo) }

            binding.btnDeleteTodo.setOnClickListener { onDelete(todo) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemTodoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Todo, newItem: Todo) = oldItem == newItem
    }
}
