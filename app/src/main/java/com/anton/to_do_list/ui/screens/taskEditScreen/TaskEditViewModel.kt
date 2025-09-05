package com.anton.to_do_list.ui.screens.taskEditScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anton.to_do_list.data.local.entity.TaskEntity
import com.anton.to_do_list.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskEditViewModel @Inject constructor(
    private val repository: TaskRepository,
    ) : ViewModel() {

    private val _state = MutableStateFlow(TaskEditState())
    val state: StateFlow<TaskEditState> = _state.asStateFlow()

    fun handleIntent(intent: TaskEditIntent) {
        when (intent) {
            is TaskEditIntent.LoadTask -> loadTask(intent.taskId)
            is TaskEditIntent.SetTitle -> _state.update { it.copy(title = intent.title) }
            is TaskEditIntent.SetDescription -> _state.update { it.copy(description = intent.description) }
            is TaskEditIntent.SetCompleted -> _state.update { it.copy(isCompleted = intent.isCompleted) }
            is TaskEditIntent.SetPhotoBase64 -> _state.update { it.copy(photoBase64 = intent.photoBase64) }
            is TaskEditIntent.OpenDatePicker -> {
                _state.update { it.copy(showDatePicker = true) }
            }
            is TaskEditIntent.DatePicked ->
                _state.update { it.copy(date = intent.date, showDatePicker = false) }

            is TaskEditIntent.DatePickerDismissed ->
                _state.update { it.copy(showDatePicker = false) }

            TaskEditIntent.SaveTask -> saveTask()
        }
    }

    private fun loadTask(taskId: Int) {
        viewModelScope.launch {
            if (taskId < 0) return@launch
            val task = repository.getTaskById(taskId) ?: return@launch

            _state.update {
                it.copy(
                    taskId = taskId,
                    title = task.title,
                    description = task.description,
                    date = task.dueDate,
                    isCompleted = task.isCompleted,
                    photoBase64 = task.photoBase64,
                )
            }
        }
    }

    private fun saveTask() {
        viewModelScope.launch {
            val currentValue = _state.value
            val taskId = currentValue.taskId

            if (taskId >= 0) {
                val existingTask = repository.getTaskById(taskId)
                if (existingTask != null) {
                    val updatedTask = existingTask.copy(
                        title = currentValue.title,
                        description = currentValue.description,
                        dueDate = currentValue.date,
                        isCompleted = currentValue.isCompleted,
                        photoBase64 = currentValue.photoBase64
                    )
                    repository.updateTask(updatedTask)
                }
            } else {
                val newTask = TaskEntity(
                    title = currentValue.title,
                    description = currentValue.description,
                    dueDate = currentValue.date,
                    isCompleted = currentValue.isCompleted,
                    photoBase64 = currentValue.photoBase64
                )
                repository.addTask(newTask)
            }
        }
    }
}
