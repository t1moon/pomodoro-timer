package com.fetecom.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fetecom.domain.Task

@Entity
data class TaskDB(
    @PrimaryKey val id: Int,
    val title: String
) {
    companion object {
        fun fromModel(task: Task) = TaskDB(
            id = task.id,
            title = task.title
        )
    }
}

fun TaskDB.toModel() = Task(id,title)