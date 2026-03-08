package com.mindful.appopening.service

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mindful.appopening.R
import com.mindful.appopening.data.model.Todo

/**
 * Simple adapter for displaying todos inside the overlay window.
 * Uses a static list since the overlay is recreated each time it appears.
 */
class OverlayTodoAdapter(private val todos: List<Todo>) :
    RecyclerView.Adapter<OverlayTodoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvText: TextView = itemView.findViewById(R.id.tvOverlayTodoText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo_overlay, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todo = todos[position]
        holder.tvText.text = todo.text
        if (todo.isCompleted) {
            holder.tvText.paintFlags =
                holder.tvText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvText.alpha = 0.5f
        } else {
            holder.tvText.paintFlags =
                holder.tvText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.tvText.alpha = 1.0f
        }
    }

    override fun getItemCount() = todos.size
}
