package com.fetecom.domain

import org.joda.time.LocalDate

interface TasksRepository {

    suspend fun getTasksByDate(date: LocalDate): List<Task>
    suspend fun getTodayTasks(): List<Task>
    suspend fun getBacklogTasks(): List<Task>
    suspend fun addTask(task: Task)
    suspend fun deleteTaskById(taskId: Int)
    suspend fun editTaskById(taskId: Int, task: Task)

    suspend fun addDoneToTaskById(taskId: Int)
    suspend fun markAsDoneByTaskId(taskId: Int, isDone: Boolean)
    suspend fun transferToTodayById(taskId: Int)
}