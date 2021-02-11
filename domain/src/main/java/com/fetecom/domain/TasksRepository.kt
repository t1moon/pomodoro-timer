package com.fetecom.domain

interface TasksRepository {

    suspend fun getTodayTasks(): List<Task>
    suspend fun getBacklogTasks(): List<Task>
    suspend fun addTask(task: Task)
    suspend fun deleteTaskById(taskId: Int)
    suspend fun editTaskById(taskId: Int, task: Task)

    suspend fun addDoneToTaskById(taskId: Int)
    suspend fun markAsDoneByTaskId(taskId: Int)
}