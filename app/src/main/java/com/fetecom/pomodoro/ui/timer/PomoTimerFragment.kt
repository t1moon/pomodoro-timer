package com.fetecom.pomodoro.ui.timer

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.fetecom.pomodoro.R
import com.fetecom.pomodoro.observe
import com.fetecom.pomodoro.ui.main.TasksViewModel
import com.fetecom.pomodoro.ui.main.TimerAdapter
import kotlinx.android.synthetic.main.tasks_fragment_task_item.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class PomoTimerFragment : Fragment(R.layout.timer_fragment) {
    private val viewModel: TasksViewModel by sharedViewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initScreen()
//        timerView.onLifecycleOwnerAttached(viewLifecycleOwner)
//        timerView.onLifecycle(this.lifecycle)
    }

    private fun initScreen() {
        viewModel.updateCurrentTaskDoneValue()
        observe(viewModel.currentTask) { task ->
            taskTitle.text = task.title
            taskTitle.alpha = if (task.isDone) 0.2f else 1f
            if (task.isDone)
                isCompleted.setBackgroundResource(R.drawable.task_done_view)
            else
                isCompleted.setBackgroundResource(R.drawable.task_not_done_view)

            timerList.adapter = TimerAdapter().apply {
                submitList(task.estimation, task.completed)
            }
            isCompleted.setOnClickListener {
                if (task.isDone)
                    isCompleted.setBackgroundResource(R.drawable.task_not_done_view)
                else
                    isCompleted.setBackgroundResource(R.drawable.task_done_view)

                viewModel.onTaskDoneOrUndone(task, !task.isDone)
                view?.findNavController()?.navigateUp()
            }
            timerList.adapter = completedAdapter
            completedAdapter.submitList(task.estimation, task.completed)
        }
    }

    private val completedAdapter= TimerAdapter()
}
