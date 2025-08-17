package com.example.taskmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmanager.viewmodel.TaskVMFactory
import com.example.taskmanager.viewmodel.TaskViewModel
import com.example.taskmanager.ui.theme.ProjectScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Get a VM instance using our simple factory (no Hilt)
            val vm: TaskViewModel = viewModel(factory = TaskVMFactory(this))

            // Show project 1 (change to 2 if you want the other project)
            ProjectScreen(vm = vm, selectedProjectId = 1)
        }
    }
}