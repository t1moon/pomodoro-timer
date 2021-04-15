package com.fetecom.pomodoro.ui.timer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.fetecom.data.Reporter
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class PomoTimerViewModel : ViewModel() {

    val focusTimeInMs = TimeUnit.MINUTES.toMillis(25)
    val focusTimeInSec = TimeUnit.MINUTES.toSeconds(25).toInt()
    val breakTimeInMs = TimeUnit.MINUTES.toMillis(5)
    val longBreakTimeInMs = TimeUnit.MINUTES.toMillis(25)


    val currentState = liveData<TimerState> {
        emit(TimerState.INIT)
    } as MutableLiveData

    val minutes = liveData<String> { } as MutableLiveData
    val seconds = liveData<String> { } as MutableLiveData
    val timeIsUp = liveData<Boolean> { } as MutableLiveData
    private val timeLeftInMs = liveData<Long> { } as MutableLiveData

    init {
        timeLeftInMs.value = focusTimeInMs
        setMinAndSec(focusTimeInMs)
    }

    private fun startTimer() {
        viewModelScope.launch {
            repeat(focusTimeInSec) {
                timeLeftInMs.value?.let {
                    setMinAndSec(it)
                    val newValue = it - 1000L
                    if (newValue < 0) {
                        timeIsUp.value = true
                        cancel()
                    }
                    else
                        timeLeftInMs.value = newValue
                }
                delay(1000L)
            }
        }
    }

    private fun setMinAndSec(it: Long) {
        val timeLeftInMinutes = TimeUnit.MILLISECONDS.toMinutes(it)
        val timeLeftInSeconds = TimeUnit.MILLISECONDS.toSeconds(it)
        val leftMinutesInSeconds = TimeUnit.MINUTES.toSeconds(timeLeftInMinutes)
        minutes.value = String.format("%02d", timeLeftInMinutes)
        seconds.value = String.format("%02d", timeLeftInSeconds - leftMinutesInSeconds)
    }

    fun onClick() {
//        Reporter.reportD("Change status from ${currentState.value}")
        startTimer()

//        val newState = when (currentState.value) {
//            TimerState.INIT -> TimerState.START
//            TimerState.FINISHED -> TimerState.START
//            TimerState.START -> TimerState.PAUSED
//            TimerState.PLAY -> TimerState.PAUSED
//            TimerState.PAUSED -> TimerState.PLAY
//            else -> throw IllegalStateException("This type doesn't exist")
//        }
//        if (newState == TimerState.START)


//        currentState.value = newState
//        Reporter.reportD("Changed status to :${currentState.value}")
    }
}

enum class TimerState {
    PAUSED, FINISHED, PLAY, START, INIT
}