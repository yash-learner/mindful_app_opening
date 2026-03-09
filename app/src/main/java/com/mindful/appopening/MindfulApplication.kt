package com.mindful.appopening

import android.app.Application
import com.mindful.appopening.data.database.AppDatabase

class MindfulApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
