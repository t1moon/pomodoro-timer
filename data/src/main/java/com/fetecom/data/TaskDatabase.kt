package com.fetecom.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fetecom.domain.Task

@Database(entities = [TaskDB::class], version = 1)
abstract class TaskDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao
}