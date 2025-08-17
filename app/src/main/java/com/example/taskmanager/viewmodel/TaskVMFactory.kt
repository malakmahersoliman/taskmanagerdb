package com.example.taskmanager.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.taskmanager.data.AppDatabase

class TaskVMFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "task_manager.db"
        ).fallbackToDestructiveMigration().build()
        return TaskViewModel(db) as T
    }
}
