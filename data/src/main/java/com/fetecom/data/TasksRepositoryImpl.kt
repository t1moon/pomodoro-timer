package com.fetecom.data

import com.fetecom.domain.Task
import com.fetecom.domain.TasksRepository
import kotlin.random.Random

class TasksRepositoryImpl(
    private val taskDao: TaskDao
) : TasksRepository {

    override suspend fun getTodayTasks(): List<Task> {
        return taskDao.getAllTasks().map { it.toModel() }
    }

    override suspend fun addTask(title: String) {
        taskDao.save(TaskDB(
            id = Random.nextInt(),
            title= title
        ))
    }

    override suspend fun deleteAllTasks() {
        taskDao.deleteAllTasks()
    }

    override suspend fun editTaskById(taskId: Int, newTitle: String, newEstimate: Int) {
        taskDao.save(
            TaskDB(
                id = taskId,
                title= newTitle
        ))
    }
}