package com.example.taskmanager.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ProjectWithTasks(
    @Embedded val project: Project,

    // 1..many (by projectId)
    @Relation(
        parentColumn = "id",
        entityColumn = "projectId"
    )
    val directTasks: List<Task>,

    // many..many via cross-ref (optional but proves the relation)
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ProjectTaskCrossRef::class,
            parentColumn = "projectId",
            entityColumn = "taskId"
        )
    )
    val sharedTasks: List<Task>
)
