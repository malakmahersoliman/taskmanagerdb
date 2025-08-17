package com.example.taskmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.taskmanager.data.AppDatabase
import com.example.taskmanager.data.Task
import kotlinx.coroutines.flow.Flow

class TaskViewModel(private val db: AppDatabase) : ViewModel() {
    val projectsLiveData = db.projectDao().getAllProjectsFlow().asLiveData()
    fun tasksFlow(projectId: Int): Flow<List<Task>> = db.taskDao().tasksForProjectFlow(projectId)
}
