package com.fetecom.pomodoro.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.fetecom.domain.Task
import com.fetecom.pomodoro.R
import com.fetecom.pomodoro.observe
import com.fetecom.pomodoro.ui.addtask.TaskDialogFragment
import com.fetecom.pomodoro.ui.main.date.DatesAdapter
import kotlinx.android.synthetic.main.tasks_fragment.*
import org.joda.time.LocalDate
import org.koin.android.viewmodel.ext.android.sharedViewModel

class TasksFragment : Fragment(R.layout.tasks_fragment) {
    private val viewModel: TasksViewModel by sharedViewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initScreen()
    }

    private fun initScreen() {
        setupUI()
        initTaskList()
        initDatesList()
        subscribeOnUpdate()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onRefresh()
    }

    private fun setupUI() {
        addTaskBtn.setOnClickListener {
            showTaskDialog()
        }
    }

    private fun showTaskDialog() {
        TaskDialogFragment.newInstance().apply {
            this.onAddOrEdit = {
                viewModel.onRefresh()
            }
        }.show(requireActivity().supportFragmentManager, TaskDialogFragment.TAG)
    }

    private fun initTaskList() {
        with(taskListView) {
            adapter = taskAdapter
        }
    }
    private fun initDatesList() {
        with(datesListView) {
            adapter = DatesAdapter(object : DatesAdapter.OuterInteractor {
                override fun onDateChosen(date: LocalDate) {
                    viewModel.onDateChosen(date)
                }
            }).apply {
                updateListWithChosenNumber()
            }
        }
    }

    private fun subscribeOnUpdate() {
        observe(viewModel.screenState) {
            if (it is TasksViewModel.ScreenState.Success)
                taskAdapter.submitList(it.taskModels)
        }
    }

    private val taskAdapter = TaskAdapter(object : TaskAdapter.Interactor {
        override fun onTaskClick(task: Task) {
            viewModel.onTaskChosen(task)
            view?.findNavController()?.navigate(R.id.action_tasksFragment_to_timerFragment)
        }

        override fun onTaskLongClick(task: Task) {
            viewModel.onTaskEdit(task)
            showTaskDialog()
        }

        override fun onTaskDoneClick(task: Task, done: Boolean) {
            viewModel.onTaskDoneOrUndone(task, done)
        }
    })

}