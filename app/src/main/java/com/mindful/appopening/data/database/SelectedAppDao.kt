package com.mindful.appopening.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindful.appopening.data.model.SelectedApp

@Dao
interface SelectedAppDao {

    @Query("SELECT * FROM selected_apps ORDER BY appName ASC")
    fun getAllSelectedApps(): LiveData<List<SelectedApp>>

    @Query("SELECT * FROM selected_apps")
    suspend fun getAllSelectedAppsList(): List<SelectedApp>

    @Query("SELECT COUNT(*) FROM selected_apps WHERE packageName = :packageName")
    suspend fun isAppSelected(packageName: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(selectedApp: SelectedApp)

    @Delete
    suspend fun delete(selectedApp: SelectedApp)

    @Query("DELETE FROM selected_apps WHERE packageName = :packageName")
    suspend fun deleteByPackageName(packageName: String)
}
