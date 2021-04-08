package com.fetecom.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fetecom.domain.Task
import org.joda.time.LocalDate
import java.util.*

@Entity
data class TaskDB(
    @PrimaryKey val id: Int,
    val title: String,
    val estimation: Int,
    val completed: Int,
    val isDone: Boolean,
    val isToday: Boolean,
    val doneAt: Long = 0,
    val createdAt: Long = 0) {
    companion object {
        fun fromModel(task: Task) = TaskDB(
            id = task.id,
            title = task.title,
            estimation = task.estimation,
            completed = task.completed,
            isDone = task.isDone,
            isToday = task.isToday,
            createdAt = System.currentTimeMillis()
        )
    }
}

fun TaskDB.toModel() = Task(
    id = id,
    title = title,
    estimation = estimation,
    completed = completed,
    isDone = isDone,
    doneAt = doneAt,
    isToday = isToday,
    created = LocalDate.fromDateFields(Date(createdAt))
)