package com.mindful.appopening.data.repository

import androidx.lifecycle.LiveData
import com.mindful.appopening.data.database.SelectedAppDao
import com.mindful.appopening.data.model.SelectedApp

class AppRepository(private val selectedAppDao: SelectedAppDao) {

    val allSelectedApps: LiveData<List<SelectedApp>> = selectedAppDao.getAllSelectedApps()

    suspend fun getAllSelectedAppsList(): List<SelectedApp> =
        selectedAppDao.getAllSelectedAppsList()

    suspend fun isAppSelected(packageName: String): Boolean =
        selectedAppDao.isAppSelected(packageName) > 0

    suspend fun addSelectedApp(selectedApp: SelectedApp) =
        selectedAppDao.insert(selectedApp)

    suspend fun removeSelectedApp(packageName: String) =
        selectedAppDao.deleteByPackageName(packageName)

    suspend fun toggleApp(selectedApp: SelectedApp, isCurrentlySelected: Boolean) {
        if (isCurrentlySelected) {
            selectedAppDao.deleteByPackageName(selectedApp.packageName)
        } else {
            selectedAppDao.insert(selectedApp)
        }
    }
}
