package com.fetecom.pomodoro.common

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel

interface ViewModelCustomView {
    val viewModel: ViewModel

    fun onLifecycleOwnerAttached(lifecycleOwner: LifecycleOwner)
}