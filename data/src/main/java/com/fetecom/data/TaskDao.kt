package com.fetecom.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface TaskDao {
    @Insert(onConflict = REPLACE)
    suspend fun save(task: TaskDB)

    @Query("SELECT * FROM taskdb ORDER BY createdAt DESC")
    suspend fun getAllTasks(): List<TaskDB>

    @Query("DELETE FROM taskdb")
    suspend fun deleteAllTasks()

    @Query("DELETE FROM taskdb WHERE id= :taskId")
    suspend fun deleteTaskById(taskId: Int)
}