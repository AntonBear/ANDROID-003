package com.anton.to_do_list.ui.screens.taskListScreen

import com.anton.to_do_list.data.local.entity.TaskEntity

data class TaskState(
    var tasks: List<TaskEntity> = emptyList(),
    val currentTask: TaskEntity? = null,
    val title: String = "",
    val description: String = "",
    val dueDate: Long = 0L,
)
