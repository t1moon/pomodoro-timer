package com.fetecom.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.fetecom.domain.Task

@Dao
interface TaskDao {
    @Insert(onConflict = REPLACE)
    suspend fun save(task: TaskDB)

    @Query("SELECT * FROM taskdb ORDER BY createdAt DESC")
    suspend fun getAllTasks(): List<TaskDB>

    @Query("SELECT * FROM taskdb WHERE id= :taskId")
    suspend fun getTaskById(taskId: Int): TaskDB

    @Query("DELETE FROM taskdb")
    suspend fun deleteAllTasks()

    @Query("DELETE FROM taskdb WHERE id= :taskId")
    suspend fun deleteTaskById(taskId: Int)

    suspend fun updateDoneInTaskById(taskId: Int) {
        val task = getTaskById(taskId)
        val updatedTask = task.copy(done = task.done + 1)
        this.updateTask(updatedTask)
    }

    @Update
    suspend fun updateTask(task: TaskDB)
}