package com.fetecom.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface TaskDao {
    @Insert(onConflict = REPLACE)
    suspend fun save(task: TaskDB)

    @Query("SELECT * FROM taskdb")
    suspend fun getAllTasks(): List<TaskDB>

}