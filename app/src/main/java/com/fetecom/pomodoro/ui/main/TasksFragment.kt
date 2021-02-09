package com.fetecom.pomodoro.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.fetecom.domain.Task
import com.fetecom.pomodoro.R
import com.fetecom.pomodoro.common.setVisible
import com.fetecom.pomodoro.observe
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.tasks_fragment.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

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
        todayDate.text = "12 April, 2021"
        addButton.setOnClickListener {
            showTaskDialog()
        }
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewModel.selectTab(tab?.position ?: 0)
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun showTaskDialog() {
        TaskDialogFragment.newInstance().apply {
            this.onAddOrEdit = {
                viewModel.onRefresh()
            }
        }.show(requireActivity().supportFragmentManager, TaskDialogFragment.TAG)
    }

    private fun initCityList() {
        taskListView.adapter = taskAdapter
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
    })
}