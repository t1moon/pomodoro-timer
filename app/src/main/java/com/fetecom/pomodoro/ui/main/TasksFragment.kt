package com.fetecom.pomodoro.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.fetecom.domain.Task
import com.fetecom.pomodoro.R
import com.fetecom.pomodoro.common.setVisible
import com.fetecom.pomodoro.observe
import kotlinx.android.synthetic.main.tasks_fragment.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class TasksFragment : Fragment(R.layout.tasks_fragment) {
    private val viewModel: TasksViewModel by sharedViewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initScreen()
    }

    private fun initScreen() {
        setupUI()
        initCityList()
        subscribeOnUpdate()
    }

    private fun setupUI() {
        addButton.setOnClickListener {
            TaskDialogFragment.newInstance()
                .apply {
                    this.onAddOrEdit = {
                        viewModel.getTasks()
                    }
                }.show(requireActivity().supportFragmentManager, TaskDialogFragment.TAG)
        }
    }

    private fun initCityList() {
        taskListView.adapter = taskAdapter
    }

    private fun subscribeOnUpdate() {
        observe(viewModel.loading) {
            taskListProgress.setVisible(it)
        }
        observe(viewModel.tasks) {
            taskAdapter.submitList(it)
        }
    }

    private val taskAdapter = TaskAdapter(object : TaskAdapter.Interactor {
        override fun onTaskClick(task: Task) {
            viewModel.onTaskChosen(task)
            view?.findNavController()?.navigate(R.id.action_tasksFragment_to_timerFragment)
        }
    })
}