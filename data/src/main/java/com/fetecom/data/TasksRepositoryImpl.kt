package com.fetecom.data

import com.fetecom.domain.Task
import com.fetecom.domain.TasksRepository
import org.joda.time.LocalDate

class TasksRepositoryImpl(
    private val taskDao: TaskDao
) : TasksRepository {

    override suspend fun getTasksByDate(date: LocalDate): List<Task> {
        return taskDao.getAllTasks().map { it.toModel() }
            .filter {
                it.created == date
            }
    }
    override suspend fun getTodayTasks(): List<Task> {
        return taskDao.getAllTasks().map { it.toModel() }
            .filter {
                it.isToday
            }
            .filterNot {
                it.isDone && !it.doneAt.isToday()
            }
    }

    override suspend fun getBacklogTasks(): List<Task> {
        return taskDao.getAllTasks().map { it.toModel() }
            .filter {
                !it.isToday
            }
    }

    override suspend fun deleteTaskById(taskId: Int) {
        taskDao.deleteTaskById(taskId)
    }

    override suspend fun addTask(task: Task) {
        taskDao.save(TaskDB.fromModel(task))
    }

    override suspend fun editTaskById(taskId: Int, task: Task) {
        taskDao.save(TaskDB.fromModel(task.copy(id = taskId)))
    }

    override suspend fun addDoneToTaskById(taskId: Int) {
        taskDao.updateDoneInTaskById(taskId)
    }

    override suspend fun markAsDoneByTaskId(taskId: Int, isDone: Boolean) {
        taskDao.markAsDoneByTaskId(taskId, isDone)
    }

    override suspend fun transferToTodayById(taskId: Int) {
        taskDao.transferToTodayById(taskId)
    }
}