package com.mindful.appopening.ui.appselect

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mindful.appopening.data.model.SelectedApp
import com.mindful.appopening.data.repository.AppRepository
import kotlinx.coroutines.launch

class AppSelectViewModel(
    private val repository: AppRepository,
    private val packageManager: PackageManager,
    private val currentPackageName: String
) : ViewModel() {

    private val _installedApps = MutableLiveData<List<InstalledApp>>()
    val installedApps: LiveData<List<InstalledApp>> = _installedApps

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadApps()
    }

    private fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val selectedPackages = repository.getAllSelectedAppsList()
                    .map { it.packageName }
                    .toSet()

                @Suppress("DEPRECATION")
                val apps = packageManager
                    .getInstalledApplications(PackageManager.GET_META_DATA)
                    .filter { appInfo ->
                        // Show only user-installed apps; exclude our own app
                        appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0 &&
                            appInfo.packageName != currentPackageName
                    }
                    .map { appInfo ->
                        InstalledApp(
                            packageName = appInfo.packageName,
                            appName = packageManager.getApplicationLabel(appInfo).toString(),
                            isSelected = appInfo.packageName in selectedPackages
                        )
                    }
                    .sortedBy { it.appName.lowercase() }

                _installedApps.postValue(apps)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun toggleAppSelection(app: InstalledApp) {
        viewModelScope.launch {
            repository.toggleApp(
                selectedApp = SelectedApp(app.packageName, app.appName),
                isCurrentlySelected = app.isSelected
            )
            // Optimistically update the local list
            val updated = _installedApps.value?.map {
                if (it.packageName == app.packageName) it.copy(isSelected = !it.isSelected)
                else it
            }
            _installedApps.postValue(updated ?: emptyList())
        }
    }

    class Factory(
        private val repository: AppRepository,
        private val packageManager: PackageManager,
        private val currentPackageName: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AppSelectViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AppSelectViewModel(repository, packageManager, currentPackageName) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
