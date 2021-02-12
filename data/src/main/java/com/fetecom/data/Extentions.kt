package com.fetecom.data

import org.joda.time.LocalDate
import java.util.*


fun LocalDate.isToday() = LocalDate.now().compareTo(this) == 0
fun Long.isToday() = LocalDate.fromDateFields(Date(this)).isToday()
