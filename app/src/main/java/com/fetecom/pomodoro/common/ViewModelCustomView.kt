package com.fetecom.pomodoro.common

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel

interface ViewModelCustomView {
    val viewModel: ViewModel

    fun onLifecycleOwnerAttached(lifecycleOwner: LifecycleOwner)
    fun onLifecycle(lifecycle: Lifecycle)
}