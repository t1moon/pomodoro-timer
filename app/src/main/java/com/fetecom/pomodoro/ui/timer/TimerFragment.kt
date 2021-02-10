package com.fetecom.pomodoro.ui.timer

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.fetecom.pomodoro.R
import com.fetecom.pomodoro.observe
import com.fetecom.pomodoro.ui.main.TasksViewModel
import com.fetecom.pomodoro.ui.main.TimerAdapter
import kotlinx.android.synthetic.main.timer_fragment.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import kotlin.math.max
import kotlin.math.min

class TimerFragment : Fragment(R.layout.timer_fragment) {
    private val viewModel: TasksViewModel by sharedViewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initTimersList()
        initScreen()
    }

    private fun initTimersList() {

    }

    private fun initScreen() {
        timerView.start()
        viewModel.updateCurrentTaskDoneValue()
        observe(viewModel.currentTask) { task ->
            chosenTaskTitle.text = task.title
            timersList.adapter = TimerAdapter().apply {
                submitList(task.estimation, task.done)
            }
        }
    }
}