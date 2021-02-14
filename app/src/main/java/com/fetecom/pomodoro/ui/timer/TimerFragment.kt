package com.fetecom.pomodoro.ui.timer

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.fetecom.domain.Task
import com.fetecom.pomodoro.R
import com.fetecom.pomodoro.observe
import com.fetecom.pomodoro.ui.main.TasksViewModel
import com.fetecom.pomodoro.ui.main.TimerAdapter
import kotlinx.android.synthetic.main.timer_fragment.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class TimerFragment : Fragment(R.layout.timer_fragment) {
    private val viewModel: TasksViewModel by sharedViewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initScreen()
    }



    private fun initScreen() {
        viewModel.updateCurrentTaskDoneValue()
        observe(viewModel.currentTask) { task ->
            chosenTaskTitle.text = task.title
            markAsCompleteBtn.setOnClickListener {
                viewModel.onCompleteTask(task)
                view?.findNavController()?.navigateUp()
            }
            initTimersList(task)
        }
    }

    private fun initTimersList(task: Task) = TimerAdapter().apply {
            timersList.adapter = this
            submitList(task.estimation, task.completed)
        }

    override fun onDestroyView() {
        timerView.releaseResources()
        super.onDestroyView()
    }
}