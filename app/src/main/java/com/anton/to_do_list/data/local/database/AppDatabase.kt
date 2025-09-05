package com.anton.to_do_list.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anton.to_do_list.data.local.dao.TaskDao
import com.anton.to_do_list.data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 9,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
