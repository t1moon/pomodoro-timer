package com.fetecom.pomodoro.ui.timer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.fetecom.data.Reporter
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ticker
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
            val tickerChannel = ticker(delayMillis = 1000, initialDelayMillis = 0)
            for (event in tickerChannel) {
                timeLeftInMs.value?.let {
                    setMinAndSec(it)
                    timeLeftInMs.value = it - 1000L
                }
            }
            tickerChannel.cancel()
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

    }
}

enum class TimerState {
    PAUSED, FINISHED, PLAY, START, INIT
}