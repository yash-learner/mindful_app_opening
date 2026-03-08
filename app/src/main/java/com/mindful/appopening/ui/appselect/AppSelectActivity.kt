package com.mindful.appopening.ui.appselect

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindful.appopening.MindfulApplication
import com.mindful.appopening.R
import com.mindful.appopening.databinding.ActivityAppSelectBinding
import com.mindful.appopening.data.repository.AppRepository

class AppSelectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppSelectBinding

    private val viewModel: AppSelectViewModel by viewModels {
        AppSelectViewModel.Factory(
            repository = AppRepository((application as MindfulApplication).database.selectedAppDao()),
            packageManager = packageManager,
            currentPackageName = packageName
        )
    }

    private lateinit var adapter: AppSelectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.title_select_apps)

        setupRecyclerView()
        setupObservers()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupRecyclerView() {
        adapter = AppSelectAdapter { app -> viewModel.toggleAppSelection(app) }
        binding.rvApps.layoutManager = LinearLayoutManager(this)
        binding.rvApps.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.rvApps.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.installedApps.observe(this) { apps ->
            adapter.submitList(apps)
            binding.tvHint.visibility = if (apps.isEmpty()) View.GONE else View.VISIBLE
        }
    }
}
