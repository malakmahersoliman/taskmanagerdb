package com.example.taskmanager.data

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.*
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.flow.collect


object DBSeed {
    private const val TAG = "DB_TEST"

    fun run(context: Context) {
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "task_manager.db"
        )
            .fallbackToDestructiveMigration()
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            // ----- Users (unique by email) -----
            val u1 = User(name = "Alice", email = "alice@acme.com")
            val u2 = User(name = "Bob", email = "bob@acme.com")

            val u1Id = db.userDao().insert(u1)
                .let { if (it == -1L) db.userDao().idByEmail(u1.email)!! else it.toInt() }
            val u2Id = db.userDao().insert(u2)
                .let { if (it == -1L) db.userDao().idByEmail(u2.email)!! else it.toInt() }

            Log.d(TAG, "Inserted (or found): User($u1Id), User($u2Id)")

            // ----- Projects (labels + createdAt use TypeConverters) -----
            val p1 =
                Project(title = "Android App", ownerId = u1Id, labels = listOf("mobile", "kotlin"))
            val p2 = Project(title = "Website", ownerId = u2Id, labels = listOf("web", "nextjs"))

            val p1Id = db.projectDao().insert(p1).let {
                if (it == -1L) db.projectDao()
                    .idByTitleOwner(p1.title, p1.ownerId)!! else it.toInt()
            }
            val p2Id = db.projectDao().insert(p2).let {
                if (it == -1L) db.projectDao()
                    .idByTitleOwner(p2.title, p2.ownerId)!! else it.toInt()
            }

            Log.d(TAG, "Inserted (or found): Project($p1Id), Project($p2Id)")

            // ----- Tasks -----
            val t1 = Task(description = "Set up Room", projectId = p1Id)
            val t2 = Task(description = "Write DAO", projectId = p1Id)
            val t3 = Task(description = "Landing page", projectId = p2Id)

            val t1Id = db.taskDao().insert(t1).let {
                if (it == -1L) db.taskDao()
                    .idByDescProject(t1.description, t1.projectId)!! else it.toInt()
            }
            val t2Id = db.taskDao().insert(t2).let {
                if (it == -1L) db.taskDao()
                    .idByDescProject(t2.description, t2.projectId)!! else it.toInt()
            }
            val t3Id = db.taskDao().insert(t3).let {
                if (it == -1L) db.taskDao()
                    .idByDescProject(t3.description, t3.projectId)!! else it.toInt()
            }

            Log.d(TAG, "Inserted (or found): Task($t1Id), Task($t2Id), Task($t3Id)")

            // ----- Attachments -----
            val a1 = Attachment(filePath = "/sdcard/Pictures/diagram.png", taskId = t1Id)
            val a2 = Attachment(filePath = "/sdcard/Documents/spec.pdf", taskId = t2Id)

            val a1Id = db.attachmentDao().insert(a1).let {
                if (it == -1L) db.attachmentDao()
                    .idByPathTask(a1.filePath, a1.taskId)!! else it.toInt()
            }
            val a2Id = db.attachmentDao().insert(a2).let {
                if (it == -1L) db.attachmentDao()
                    .idByPathTask(a2.filePath, a2.taskId)!! else it.toInt()
            }

            Log.d(TAG, "Inserted (or found): Attachment($a1Id), Attachment($a2Id)")

            // ----- CrossRef (composite PK; IGNORE avoids duplicate crash) -----
            db.projectDao().insertCrossRef(ProjectTaskCrossRef(projectId = p2Id, taskId = t2Id))
            Log.d(TAG, "Inserted (or ignored): CrossRef(Project=$p2Id, Task=$t2Id)")


            Log.d(TAG, "Users: ${db.userDao().getAll()}")
            Log.d(TAG, "Projects: ${db.projectDao().getAll()}")
            Log.d(TAG, "Tasks: ${db.taskDao().getAll()}")
            Log.d(TAG, "Attachments: ${db.attachmentDao().getAll()}")

            //  raw query example
            val raw = SimpleSQLiteQuery(
                "SELECT p.* FROM projects p WHERE (SELECT COUNT(*) FROM tasks t WHERE t.projectId = p.id) > ?",
                arrayOf(1)
            )
            Log.d(
                TAG,
                "Raw projectsWithMoreThan(1): ${db.projectDao().projectsWithMoreThanRaw(raw)}"
            )

            // Relation test (converters + embedded objects) -----
            val p1With = db.projectDao().getProjectWithTasks(p1Id)
            Log.d(TAG, "Project with Tasks: $p1With")

            try {
                Log.d(TAG, "2.4 demo START")

                // 1) Suspend once (one-time snapshot)
                val snapshot = db.projectDao().getAllProjectsOnce()
                Log.d(TAG, "Suspend projects: $snapshot")

                // 2) Flow: collect for ~3 seconds and log emissions
                withTimeoutOrNull(3000) {
                    // Force an update after 300ms to prove Flow re-emits
                    launch {
                        delay(300)
                        db.projectDao().insert(Project(title = "Temp-Demo", ownerId = 1))
                    }
                    db.projectDao().getAllProjectsFlow().collect { projects ->
                        Log.d(TAG, "Flow emission: $projects")
                    }
                }

                Log.d(TAG, "2.4 demo END")
// ---- 2.6: Perf comparison ----
                Log.d(TAG, "2.6 PERF START")
                val iterations = 100

// Warm-up
                db.projectDao().projectsWithMoreThan(1)
                db.projectDao().projectsWithMoreThanRaw(
                    SimpleSQLiteQuery(
                        "SELECT p.* FROM projects p WHERE (SELECT COUNT(*) FROM tasks t WHERE t.projectId = p.id) > ?",
                        arrayOf(1)
                    )
                )

                // Measure @Query
                var start = System.nanoTime()
                repeat(iterations) {
                    db.projectDao().projectsWithMoreThan(1)
                }
                val queryNs = System.nanoTime() - start
                Log.d(TAG, "PERF: Room @Query x$iterations: ${queryNs}ns")

                // Measure @RawQuery
                val rawSql = SimpleSQLiteQuery(
                    "SELECT p.* FROM projects p WHERE (SELECT COUNT(*) FROM tasks t WHERE t.projectId = p.id) > ?",
                    arrayOf(1)
                )
                start = System.nanoTime()
                repeat(iterations) {
                    db.projectDao().projectsWithMoreThanRaw(rawSql)
                }
                val rawNs = System.nanoTime() - start
                Log.d(TAG, "PERF: Room @RawQuery x$iterations: ${rawNs}ns")

                Log.d(TAG, "2.6 PERF END")

                // Cleanup temp rows
                db.projectDao().deleteTempProjects()
                Log.d(TAG, "Deleted temp projects")

            } catch (t: Throwable) {
                Log.e(TAG, "2.4 demo ERROR", t)
            }

        }
    }
}

