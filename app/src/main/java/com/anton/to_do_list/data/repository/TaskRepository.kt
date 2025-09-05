package com.anton.to_do_list.data.repository

import android.util.Log
import com.anton.to_do_list.data.local.dao.TaskDao
import com.anton.to_do_list.data.local.entity.SyncStatus
import com.anton.to_do_list.data.local.entity.TaskEntity
import com.anton.to_do_list.data.network.TaskApi
import com.anton.to_do_list.data.network.TaskDto
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val taskApi: TaskApi,
) {

    suspend fun syncPendingTasks() {
        val unsynced = taskDao.getPendingTasks()

        for (task in unsynced) {
            try {
                when (task.syncStatus) {
                    SyncStatus.PENDING_CREATE -> {
                        val dto = taskEntityToDto(task)
                        val response = taskApi.addTask(dto)
                        if (response.isSuccessful) {
                            val created = response.body()?.values?.lastOrNull()
                            if (created != null) {
                                val updatedTask = task.copy(
                                    remoteId = created.id,
                                    syncStatus = SyncStatus.SYNCED
                                )
                                taskDao.update(updatedTask)
                            }
                        }
                    }

                    SyncStatus.PENDING_UPDATE -> {
                        if (task.remoteId == null) {
                            taskDao.update(task.copy(syncStatus = SyncStatus.PENDING_CREATE))
                        }
                        val dto = taskEntityToDto(task)
                        val response = taskApi.updateTask(dto)

                        if (response.isSuccessful) {
                            val serverTask = response.body()?.values?.firstOrNull()
                            if (serverTask != null) {
                                val updatedTask = task.copy(
                                    remoteId = task.remoteId,
                                    syncStatus = SyncStatus.SYNCED,
                                    dueDate = parseDate(serverTask.date),
                                    photoBase64 = serverTask.photoBase64
                                )
                                taskDao.update(updatedTask)
                            }
                        } else {
                            Log.e("TaskRepository", "Update failed, code: ${response.code()}")
                        }
                    }

                    SyncStatus.PENDING_DELETE -> {
                        // delete api
                    }

                    else -> Unit
                }
            } catch (e: Exception) {
                return
            }
        }
    }

    suspend fun getTasks(): List<TaskEntity> {
        return try {
            val response = taskApi.getTasks()
            if (response.isSuccessful) {
                val body = response.body()
                body?.values?.map { dto ->
                    TaskEntity(
                        remoteId = dto.id,
                        title = dto.name,
                        description = "",
                        dueDate = parseDate(dto.date),
                        isCompleted = dto.completed,
                        photoBase64 = dto.photoBase64,
                        syncStatus = SyncStatus.SYNCED,
                    )
                } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getAllTasks() = taskDao.getAll()

    suspend fun getTaskById(taskId: Int): TaskEntity? = taskDao.getTaskById(taskId)

    suspend fun addTask(task: TaskEntity) {
        val localId = taskDao.insert(task).toInt()
        try {
            val dto = TaskDto(
                name = task.title,
                completed = task.isCompleted,
                photoBase64 = task.photoBase64
            )

            val response = taskApi.addTask(dto)
            if (response.isSuccessful) {
                val createdTask = response.body()?.values?.lastOrNull()
                if (createdTask != null) {
                    taskDao.updateRemoteId(localId, createdTask.id)
                }
            } else {
                taskDao.update(
                    task.copy(id = localId, syncStatus = SyncStatus.PENDING_CREATE)
                )
            }
        } catch (e: Exception) {
            taskDao.update(
                task.copy(id = localId, syncStatus = SyncStatus.PENDING_CREATE)
            )
        }
    }

    suspend fun updateTask(task: TaskEntity) {
        Log.d("updateTask", "Starting update for task: ${task.title}, localId=${task.id}, remoteId=${task.remoteId}")

        val localTask: TaskEntity = if (task.remoteId == null) {
            val pendingCreateTask = task.copy(syncStatus = SyncStatus.PENDING_CREATE)
            taskDao.update(pendingCreateTask)
            Log.d("updateTask", "Task marked as PENDING_CREATE locally: ${task.title}")
            return
        } else {
            val pendingUpdateTask = task.copy(syncStatus = SyncStatus.PENDING_UPDATE)
            taskDao.update(pendingUpdateTask)
            pendingUpdateTask
        }

        try {
            val dto = TaskDto(
                id = localTask.remoteId?.toString(),
                name = localTask.title,  // пустое допустимо
                completed = localTask.isCompleted,
                photoBase64 = localTask.photoBase64
            )
            Log.d("updateTask", "Prepared DTO: $dto")

            val response = taskApi.updateTask(dto)

            if (response.isSuccessful) {
                val serverTask = response.body()?.values?.find { it.name == localTask.title } ?: return
                val syncedTask = localTask.copy(
                    remoteId = serverTask.id,
                    syncStatus = SyncStatus.SYNCED,
                    dueDate = parseDate(serverTask.date),
                    photoBase64 = serverTask.photoBase64
                )
                taskDao.update(syncedTask)
                Log.d("updateTask", "Task synced successfully: ${syncedTask.title}")
            } else {
                Log.e("updateTask", "Update failed. HTTP code: ${response.code()}, body: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("updateTask", "Exception during updateTask", e)
        }
    }



    suspend fun deleteTask(task: TaskEntity) {
        taskDao.delete(task)
    }

    private fun parseDate(dateStr: String): Long {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            format.parse(dateStr)?.time ?: System.currentTimeMillis()
        } catch (_: Exception) {
            System.currentTimeMillis()
        }
    }
    private fun taskEntityToDto(task: TaskEntity): TaskDto {
        return TaskDto(
            id = task.remoteId.toString(),
            name = task.title,
            completed = task.isCompleted,
            photoBase64 = task.photoBase64
        )
    }
}
