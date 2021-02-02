package com.fetecom.domain

interface TasksRepository {

    suspend fun getTodayTasks(): List<Task>
    suspend fun addTask(title: String)
    suspend fun deleteAllTasks()
    suspend fun editTaskById(taskId: Int, newTitle: String, newEstimate: Int)
}