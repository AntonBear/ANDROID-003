package com.anton.to_do_list.ui.screens.taskEditScreen

sealed interface TaskEditIntent {
    data class SetTitle(val title: String) : TaskEditIntent
    data class SetDescription(val description: String) : TaskEditIntent
    data class LoadTask(val taskId: Int) : TaskEditIntent
    data object SaveTask : TaskEditIntent
    object OpenDatePicker : TaskEditIntent
    data class DatePicked(val date: Long) : TaskEditIntent
    object DatePickerDismissed : TaskEditIntent
    data class SetCompleted(val isCompleted: Boolean) : TaskEditIntent
    data class SetPhotoBase64(val photoBase64: String?) : TaskEditIntent
}
