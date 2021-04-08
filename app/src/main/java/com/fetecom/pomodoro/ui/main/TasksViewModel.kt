package com.fetecom.pomodoro.ui.main

import androidx.lifecycle.*
import com.fetecom.data.Reporter
import com.fetecom.domain.Task
import com.fetecom.domain.TasksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.LocalDate

class TasksViewModel(
    private val tasksRepository: TasksRepository
) : ViewModel() {
    sealed class ScreenState {
        object Loading : ScreenState()
        data class Success(val taskModels: List<TaskAdapter.TaskModel>) : ScreenState()
    }

    private val selectedDate: MutableLiveData<LocalDate> = liveData {
        emit(LocalDate.now())
    } as MutableLiveData<LocalDate>

    private val taskList = liveData<List<Task>> { } as MutableLiveData

    val screenState = Transformations.switchMap(selectedDate) {
        liveData(Dispatchers.IO) {
            val tasks = tasksRepository.getTasksByDate(it)
            taskList.postValue(tasks)
            Reporter.reportD("Tasks have been received: $tasks")
            emit(ScreenState.Success(tasks.map { TaskAdapter.TaskModel(it) }))
        }
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
            tasksRepository.addTask(
                Task(
                    title = title,
                    estimation = estimationOfAddingTask.value ?: 1
                )
            )
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
        selectedDate.value = selectedDate.value
    }

    fun updateCurrentTaskDoneValue() {
        currentTask.value?.let { task ->
            viewModelScope.launch {
                tasksRepository.addDoneToTaskById(task.id)
                Reporter.reportD("Task has increased done value: ${task.title}")
            }
        }

    }


    fun onCompleteTask(task: Task) {
        viewModelScope.launch {
            tasksRepository.markAsDoneByTaskId(task.id)
            Reporter.reportD("Task marked as done: ${task.title}")
            onRefresh()
        }
    }

    fun onDateChosen(date: LocalDate) {
        Reporter.reportD("Date is selected: ${date.toString("MMM dd")}")
        selectedDate.value = date
        onRefresh()
    }


}