package com.example.taskmanager.ui.theme


import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskmanager.viewmodel.TaskViewModel

@Composable
fun ProjectScreen(vm: TaskViewModel, selectedProjectId: Int) {
    val projects by vm.projectsLiveData.observeAsState(emptyList())
    val tasks by vm.tasksFlow(selectedProjectId).collectAsStateWithLifecycle(initialValue = emptyList())

    LaunchedEffect(projects) { Log.d("UI", "LiveData projects observed: $projects") }
    LaunchedEffect(tasks)    { Log.d("UI", "Flow tasks collected: $tasks") }

    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Projects card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = "Projects (${projects.size})",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        projects.forEachIndexed { i, p ->
                            Text("• ${p.title}", style = MaterialTheme.typography.bodyLarge)
                            if (i != projects.lastIndex) {
                                Spacer(Modifier.height(8.dp))
                                HorizontalDivider(
                                    Modifier,
                                    DividerDefaults.Thickness,
                                    DividerDefaults.color
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }

            // Tasks card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = "Tasks in project $selectedProjectId (${tasks.size})",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        tasks.forEachIndexed { i, t ->
                            Text("– ${t.description}", style = MaterialTheme.typography.bodyLarge)
                            if (i != tasks.lastIndex) {
                                Spacer(Modifier.height(8.dp))
                                HorizontalDivider(
                                    Modifier,
                                    DividerDefaults.Thickness,
                                    DividerDefaults.color
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
