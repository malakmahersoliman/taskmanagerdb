package com.example.taskmanager.data

import androidx.room.*

// -------- Users --------
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)] // unique email
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String
)

// -------- Projects --------
@Entity(
    tableName = "projects",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("ownerId"),
        Index(value = ["title", "ownerId"], unique = true) // prevent dup projects per owner
    ]
)
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val ownerId: Int,
    // Uses your TypeConverters (List<String>, Date) — make sure AppDatabase has @TypeConverters(Converters::class)
    val labels: List<String> = emptyList(),
    val createdAt: java.util.Date = java.util.Date()
)

// -------- Tasks --------
@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("projectId"),
        Index(value = ["description", "projectId"], unique = true) // prevent dup tasks within a project
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    val projectId: Int
)

// -------- Attachments --------
@Entity(
    tableName = "attachments",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("taskId"),
        Index(value = ["filePath", "taskId"], unique = true) // prevent dup file for same task
    ]
)
data class Attachment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val filePath: String,
    val taskId: Int
)

// -------- Many-to-many: Project <-> Task --------
@Entity(
    tableName = "project_task_cross_ref",
    primaryKeys = ["projectId", "taskId"], // composite PK prevents duplicates
    indices = [Index("projectId"), Index("taskId")],
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ProjectTaskCrossRef(
    val projectId: Int,
    val taskId: Int
)
