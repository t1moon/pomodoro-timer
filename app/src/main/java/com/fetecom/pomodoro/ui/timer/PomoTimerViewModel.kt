package com.fetecom.pomodoro.ui.timer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.fetecom.data.Reporter
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ticker
import org.joda.time.LocalDateTime

class PomoTimerViewModel : ViewModel() {

    private val _currentProgress = liveData {
        emit(0f)
    } as MutableLiveData
    var currentProgress = _currentProgress.value ?: 100f

    val newProgress = liveData<Float> { } as MutableLiveData
    val currentState = liveData<TimerState> {} as MutableLiveData


    fun onClick() {
        Reporter.reportD("Change status from ${currentState.value}")
        val newState = when (currentState.value) {
            TimerState.INIT -> TimerState.START
            TimerState.FINISHED -> TimerState.START
            TimerState.START -> TimerState.PAUSED
            TimerState.PLAY -> TimerState.PAUSED
            TimerState.PAUSED -> TimerState.PLAY
            else -> throw IllegalStateException("This type doesn't exist")
        }
        currentState.value = newState
        Reporter.reportD("Changed status to :${currentState.value}")
    }

    fun onLongClick() {
        currentState.value = TimerState.FINISHED
    }

    private val timerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    fun onInit() {
        currentState.value = TimerState.INIT
        timerScope.launch {
                repeat(100) { index ->
                    delay(1000L)
                    updateProgress(100f - index)
                }
        }
    }

    fun updateProgress(newProgress: Float) {
        Reporter.reportD("New timer progress: $newProgress")
        this.newProgress.postValue(newProgress)
    }

    fun releaseTimer() {
        timerScope.cancel()
    }
}

enum class TimerState {
    PAUSED, FINISHED, PLAY, START, INIT
}