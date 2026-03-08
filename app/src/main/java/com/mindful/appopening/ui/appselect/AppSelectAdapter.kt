package com.mindful.appopening.ui.appselect

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mindful.appopening.databinding.ItemAppSelectBinding

class AppSelectAdapter(
    private val onToggle: (InstalledApp) -> Unit
) : ListAdapter<InstalledApp, AppSelectAdapter.AppViewHolder>(DiffCallback) {

    inner class AppViewHolder(private val binding: ItemAppSelectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(app: InstalledApp) {
            binding.tvAppName.text = app.appName
            binding.tvPackageName.text = app.packageName

            // Avoid triggering listener during rebind
            binding.swAppSelected.setOnCheckedChangeListener(null)
            binding.swAppSelected.isChecked = app.isSelected

            binding.swAppSelected.setOnCheckedChangeListener { _, _ -> onToggle(app) }
            binding.root.setOnClickListener { onToggle(app) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemAppSelectBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AppViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<InstalledApp>() {
        override fun areItemsTheSame(oldItem: InstalledApp, newItem: InstalledApp) =
            oldItem.packageName == newItem.packageName

        override fun areContentsTheSame(oldItem: InstalledApp, newItem: InstalledApp) =
            oldItem == newItem
    }
}
