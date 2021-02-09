package com.fetecom.domain

import kotlin.random.Random

data class Task(
    val id: Int = Random.nextInt(),
    val title: String,
    val estimation: Int,
    val isDone: Boolean = false
)
