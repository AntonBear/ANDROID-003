package com.anton.to_do_list.ui.screens.taskListScreen

import android.util.Log
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
class TaskListViewModel @Inject constructor(
    private val repository: TaskRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(TaskState())
    val state: StateFlow<TaskState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllTasks().collect { tasks ->
                _state.update { it.copy(tasks = tasks) }
            }
        }

        viewModelScope.launch {
            try {
                val remoteTasks = repository.getTasks()
                _state.update { it.copy(tasks = remoteTasks) }
            } catch (e: Exception) {
                Log.e("TaskListViewModel", "Failed to fetch remote tasks", e)
            }
        }
    }


    fun handleIntent(intent: TaskIntent) {
        when (intent) {
            is TaskIntent.AddTask -> addTask(intent.title, intent.description, intent.dueDate)
            is TaskIntent.UpdateTask -> updateTask(intent.task)
            is TaskIntent.DeleteTask -> deleteTask(intent.task)
        }
    }

    private fun addTask(title: String, description: String, dueDate: Long) {
        viewModelScope.launch {
            val task = TaskEntity(title = title, description = description, dueDate = dueDate)
            repository.addTask(task)
            _state.update { it.copy(title = "", description = "") }
        }
    }

    private fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task)
            _state.update { it.copy(currentTask = null) }
        }
    }

    private fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }
}
