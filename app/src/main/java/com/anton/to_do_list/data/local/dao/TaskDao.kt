package com.anton.to_do_list.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.anton.to_do_list.data.local.entity.SyncStatus
import com.anton.to_do_list.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks")
    fun getAll(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Query("SELECT * FROM tasks")
    suspend fun getAllNow(): List<TaskEntity>

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: Int): TaskEntity?

    @Query("UPDATE tasks SET remoteId = :serverId, syncStatus = :status, dueDate = :dueDate WHERE id = :localId OR remoteId = :serverId")
    suspend fun updateByRemoteId(localId: Int? = null, serverId: Int, status: SyncStatus, dueDate: Long)

    @Query("SELECT * FROM tasks WHERE syncStatus != :status")
    suspend fun getPendingTasks(status: SyncStatus = SyncStatus.SYNCED): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getByRemoteId(remoteId: Int): TaskEntity?

    @Query("UPDATE tasks SET remoteId = :remoteId, syncStatus = :syncStatus WHERE id = :localId")
    suspend fun updateRemoteId(localId: Int, remoteId: Int, syncStatus: SyncStatus = SyncStatus.SYNCED)

}
