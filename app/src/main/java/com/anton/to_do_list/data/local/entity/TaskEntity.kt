package com.anton.to_do_list.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = 0,
    val remoteId: Int? = null,
    val title: String,
    val description: String,
    val dueDate: Long,
    val isCompleted: Boolean = false,
    val photoBase64: String? = null,
    val syncStatus: SyncStatus = SyncStatus.PENDING_CREATE
)

enum class SyncStatus {
    SYNCED,
    PENDING_CREATE,
    PENDING_UPDATE,
    PENDING_DELETE,
    CONFLICT,
}
