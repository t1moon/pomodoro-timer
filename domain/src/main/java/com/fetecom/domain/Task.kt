package com.fetecom.domain

import org.joda.time.LocalDate
import kotlin.random.Random

data class Task(
    val id: Int = Random.nextInt(),
    val title: String,
    val estimation: Int,
    val completed: Int = 0,
    val isDone: Boolean = false,
    val doneAt: Long = 0,
    val isToday: Boolean = false,
    val created: LocalDate = LocalDate.now()
)
