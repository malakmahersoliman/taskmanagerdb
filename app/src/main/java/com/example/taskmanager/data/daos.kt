package com.example.taskmanager.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import androidx.sqlite.db.SupportSQLiteQuery

// ---------- User ----------
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User): Long

    @Query("SELECT id FROM users WHERE email = :email LIMIT 1")
    suspend fun idByEmail(email: String): Int?

    @Query("SELECT * FROM users ORDER BY id")
    suspend fun getAll(): List<User>
}

// ---------- Project ----------
@Dao
interface ProjectDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(project: Project): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(ref: ProjectTaskCrossRef)

    @Query("SELECT id FROM projects WHERE title = :title AND ownerId = :ownerId LIMIT 1")
    suspend fun idByTitleOwner(title: String, ownerId: Int): Int?

    @Query("SELECT * FROM projects ORDER BY id")
    suspend fun getAll(): List<Project>


    @Query("SELECT * FROM projects ORDER BY id")
    fun getAllFlow(): Flow<List<Project>>

    @Query("""
        SELECT p.* FROM projects p
        WHERE (SELECT COUNT(*) FROM tasks t WHERE t.projectId = p.id) > :min
    """)
    suspend fun projectsWithMoreThan(min: Int): List<Project>

    @RawQuery
    suspend fun projectsWithMoreThanRaw(query: SupportSQLiteQuery): List<Project>

    @Transaction
    @Query("SELECT * FROM projects WHERE id = :projectId")
    suspend fun getProjectWithTasks(projectId: Int): ProjectWithTasks

    @Query("SELECT * FROM projects ORDER BY id")
    suspend fun getAllProjectsOnce(): List<Project>   // one-time snapshot

    @Query("SELECT * FROM projects ORDER BY id")
    fun getAllProjectsFlow(): kotlinx.coroutines.flow.Flow<List<Project>>

    @Query("DELETE FROM projects WHERE title LIKE 'Temp %' OR title = 'Temp-Demo'")
    suspend fun deleteTempProjects()


}

// ---------- Task ----------
@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: Task): Long

    @Query("SELECT id FROM tasks WHERE description = :desc AND projectId = :projectId LIMIT 1")
    suspend fun idByDescProject(desc: String, projectId: Int): Int?

    @Query("SELECT * FROM tasks ORDER BY id")
    suspend fun getAll(): List<Task>

    @Query("SELECT * FROM tasks WHERE projectId = :projectId ORDER BY id")
    fun tasksForProjectFlow(projectId: Int): Flow<List<Task>>


}

// ---------- Attachment ----------
@Dao
interface AttachmentDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(attachment: Attachment): Long

    @Query("SELECT id FROM attachments WHERE filePath = :path AND taskId = :taskId LIMIT 1")
    suspend fun idByPathTask(path: String, taskId: Int): Int?

    @Query("SELECT * FROM attachments ORDER BY id")
    suspend fun getAll(): List<Attachment>
}
