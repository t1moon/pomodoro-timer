package com.fetecom.pomodoro.ui.timer

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.fetecom.pomodoro.R
import com.fetecom.pomodoro.observe
import com.fetecom.pomodoro.ui.main.TasksViewModel
import kotlinx.android.synthetic.main.timer_fragment.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class TimerFragment : Fragment(R.layout.timer_fragment) {
    private val viewModel: TasksViewModel by sharedViewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initScreen()
    }

    private fun initScreen() {
        timerView.start()
        observe(viewModel.currentTask) {
            chosenTaskTitle.text = it.title
        }
    }
}