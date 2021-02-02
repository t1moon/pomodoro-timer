package com.fetecom.pomodoro.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.fetecom.data.Reporter
import com.fetecom.domain.Task
import com.fetecom.domain.TasksRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class TasksViewModel(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    val loading = liveData<Boolean> { } as MutableLiveData
    val tasks = liveData<List<TaskAdapter.TaskModel>> {} as MutableLiveData

    init {
        addNewTask("New task 1")
        addNewTask("New task 2")
        addNewTask("New task 3")
        getTasks()
    }

    private fun getTasks() {
        loading.value = true
        viewModelScope.launch {
            val receivedTasks = tasksRepository.getTodayTasks()
            loading.value = false
            val taskUiModels = receivedTasks.map { TaskAdapter.TaskModel(it) }
            this@TasksViewModel.tasks.value = taskUiModels
            Reporter.reportD("Tasks has been received: ${receivedTasks.size}")
        }
    }

    fun onTaskChosen(task: Task) {
        viewModelScope.launch {
            Reporter.reportD("Task has been chosen: ${task.title}")

        }
    }

    fun addNewTask(title: String) {
        viewModelScope.launch {
            tasksRepository.addTask(title)
            Reporter.reportD("Task has been added: $title")

        }
    }
}