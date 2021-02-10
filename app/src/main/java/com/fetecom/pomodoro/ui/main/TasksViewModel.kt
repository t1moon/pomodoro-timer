package com.fetecom.pomodoro.ui.main

import androidx.lifecycle.*
import com.fetecom.data.Reporter
import com.fetecom.domain.Task
import com.fetecom.domain.TasksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TasksViewModel(
    private val tasksRepository: TasksRepository
) : ViewModel() {
    sealed class ScreenState {
        object Loading : ScreenState()
        data class Success(val taskModels: List<TaskAdapter.TaskModel>) : ScreenState()
    }


    private val selectedTab: MutableLiveData<TabType> = liveData {
        emit(TabType.Today)
    } as MutableLiveData<TabType>

    val taskList = liveData<List<Task>> { } as MutableLiveData
    val screenState = Transformations.switchMap(selectedTab) {
        liveData(Dispatchers.IO) {
            val tasks = when (it) {
                TabType.Today -> tasksRepository.getTodayTasks()
                TabType.Backlog -> tasksRepository.getBacklogTasks()
                null -> tasksRepository.getTodayTasks()
            }
            taskList.postValue(tasks)
            Reporter.reportD("Tasks have been received: ${tasks.size}")

            emit(ScreenState.Success(tasks.map { TaskAdapter.TaskModel(it) }))
        }
    }

    fun selectTab(tabPosition: Int) {
        selectedTab.value = if (tabPosition == 0) TabType.Today else TabType.Backlog
        Reporter.reportD("Tab is selected: ${selectedTab.value.toString()}")
    }

    enum class TabType {
        Today, Backlog
    }

    val currentTask = liveData<Task> {} as MutableLiveData
    val editableTask = liveData<Task> {} as MutableLiveData
    var estimationOfAddingTask = liveData<Int> {} as MutableLiveData

    fun deleteEditableTask() {
        editableTask.value?.id?.let { taskId ->
            viewModelScope.launch {
                tasksRepository.deleteTaskById(taskId)
            }
        }

    }


    fun onTaskChosen(task: Task) {
        currentTask.value = task
        viewModelScope.launch {
            Reporter.reportD("Task has been chosen: ${task.title}")
        }
    }

    fun addNewTask(title: String, estimation: Int) {
        viewModelScope.launch {
            tasksRepository.addTask(Task(title = title, estimation = estimationOfAddingTask.value ?: 1))
            Reporter.reportD("Task has been added: $title")
        }
    }

    fun editTask(taskId: Int, title: String, estimation: Int) {
        viewModelScope.launch {
            tasksRepository.editTaskById(taskId, Task(title = title, estimation = estimation))
            Reporter.reportD("Task has been edited: $title")
        }
    }

    fun onTaskEdit(task: Task) {
        editableTask.value = task
    }

    fun onRefresh() {
        selectedTab.value = selectedTab.value
    }

    fun updateCurrentTaskDoneValue() {
        currentTask.value?.let { task ->
            viewModelScope.launch {
                tasksRepository.addDoneToTaskById(task.id)
                Reporter.reportD("Task has increased done value: ${task.title}")
            }
        }

    }


}