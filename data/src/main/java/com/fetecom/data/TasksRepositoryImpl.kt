package com.fetecom.data

import com.fetecom.domain.Task
import com.fetecom.domain.TasksRepository
import kotlin.random.Random

class TasksRepositoryImpl(
    private val taskDao: TaskDao
) : TasksRepository {

    override suspend fun getTodayTasks(): List<Task> {
        return taskDao.getAllTasks()
            .map { it.toModel() }
            .sortedBy {
                it.done
            }
    }

    override suspend fun getBacklogTasks(): List<Task> {
        return taskDao.getAllTasks().map { it.toModel() }
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

    override suspend fun markAsDoneByTaskId(taskId: Int) {
        taskDao.markAsDoneByTaskId(taskId)
    }
}