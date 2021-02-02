package com.fetecom.domain

interface TasksRepository {

    suspend fun getTodayTasks(): List<Task>
    suspend fun addTask(title: String)
}