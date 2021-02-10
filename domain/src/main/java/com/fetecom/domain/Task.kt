package com.fetecom.domain

import kotlin.random.Random

data class Task(
    val id: Int = Random.nextInt(),
    val title: String,
    val estimation: Int,
    val done: Int = 0,
    val isDone: Boolean = false
)
