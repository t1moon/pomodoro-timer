package com.fetecom.pomodoro.ui.timer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData

class PomoTimerViewModel : ViewModel() {

    val currentProgress = liveData {
        emit(100f)
    } as MutableLiveData


}