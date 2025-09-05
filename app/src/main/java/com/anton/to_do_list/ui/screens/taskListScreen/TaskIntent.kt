package com.anton.to_do_list.ui.screens.taskListScreen

import com.anton.to_do_list.data.local.entity.TaskEntity

sealed class TaskIntent {
    data class AddTask(val title: String, val description: String, val dueDate: Long) : TaskIntent()
    data class UpdateTask(val task: TaskEntity) : TaskIntent()
    data class DeleteTask(val task: TaskEntity) : TaskIntent()
}
