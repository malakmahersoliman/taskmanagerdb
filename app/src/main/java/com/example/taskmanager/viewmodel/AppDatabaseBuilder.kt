package com.example.taskmanager.ui

import android.content.Context
import androidx.room.Room
import com.example.taskmanager.data.AppDatabase

object AppDatabaseBuilder {
    @Volatile private var instance: AppDatabase? = null
    fun hold(ctx: Context): AppDatabase =
        instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                ctx.applicationContext,
                AppDatabase::class.java,
                "task_manager.db"
            ).fallbackToDestructiveMigration().build().also { instance = it }
        }
}
