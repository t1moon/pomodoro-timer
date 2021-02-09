package com.fetecom.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fetecom.domain.Task

@Entity
data class TaskDB(
    @PrimaryKey val id: Int,
    val title: String,
    val estimation: Int,
    val isDone: Boolean,
    val createdAt: Long) {
    companion object {
        fun fromModel(task: Task) = TaskDB(
            id = task.id,
            title = task.title,
            estimation = task.estimation,
            isDone = task.isDone,
            createdAt = System.currentTimeMillis()
        )
    }
}

fun TaskDB.toModel() = Task(
    id = id,
    title = title,
    estimation = estimation,
    isDone = isDone)