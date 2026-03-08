package com.mindful.appopening.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mindful.appopening.data.model.SelectedApp
import com.mindful.appopening.data.model.Todo

@Database(
    entities = [Todo::class, SelectedApp::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao
    abstract fun selectedAppDao(): SelectedAppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mindful_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
