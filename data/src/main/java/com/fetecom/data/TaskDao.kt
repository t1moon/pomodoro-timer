package com.fetecom.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    @Insert(onConflict = REPLACE)
    suspend fun save(task: TaskDB)

    @Query("SELECT * FROM taskdb")
    suspend fun getAllTasks(): List<TaskDB>

    @Query("SELECT * FROM taskdb WHERE id= :taskId")
    suspend fun getTaskById(taskId: Int): TaskDB

    @Query("DELETE FROM taskdb")
    suspend fun deleteAllTasks()

    @Query("DELETE FROM taskdb WHERE id= :taskId")
    suspend fun deleteTaskById(taskId: Int)

    suspend fun updateDoneInTaskById(taskId: Int) {
        val task = getTaskById(taskId)
        save(task.copy(completed = task.completed + 1))
    }

    suspend fun markAsDoneByTaskId(taskId: Int) {
        val task = getTaskById(taskId)
        save(task.copy(isDone = true, doneAt = System.currentTimeMillis()))
    }

    suspend fun transferToTodayById(taskId: Int) {
        val task = getTaskById(taskId)
        save(task.copy(isToday = true))
    }
}