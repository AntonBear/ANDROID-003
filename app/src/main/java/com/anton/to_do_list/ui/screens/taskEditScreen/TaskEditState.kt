package com.anton.to_do_list.ui.screens.taskEditScreen

data class TaskEditState(
    val taskId: Int = -1,
    val remoteId: Int? = null,
    val title: String = "",
    val description: String = "",
    val date: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val photoBase64: String? = null,
    val showDatePicker: Boolean = false,
)
