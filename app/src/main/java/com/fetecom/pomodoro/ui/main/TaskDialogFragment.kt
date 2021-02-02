package com.fetecom.pomodoro.ui.main

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fetecom.pomodoro.R
import com.fetecom.pomodoro.common.hide
import com.fetecom.pomodoro.common.show
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.tasks_fragment_task_dialog.*
import org.koin.android.viewmodel.ext.android.sharedViewModel


open class TaskDialogFragment : BottomSheetDialogFragment() {
    private val viewModel: TasksViewModel by sharedViewModel()

    var onAddOrEdit: () -> Unit = {}

    companion object {
        const val TASK_ID = "TASK_ID"
        const val TAG = "TaskDialogFragment"
        @JvmStatic
        fun newInstance(taskId: Int? = null) = TaskDialogFragment().apply {
            arguments = Bundle().apply {
                taskId?.let {putInt(TASK_ID, taskId)}
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.tasks_fragment_task_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val taskId = arguments?.getInt(TASK_ID, -1) ?: -1

        if (taskId != -1)
            initTask(taskId)
        else
            initEmpty()
    }

    private fun initTask(taskId: Int) {
        viewModel.tasks.value?.find { it.task.id == taskId }?.task?.title?.let {
            titleInput.setText(it)
            deleteIcon.show()
            addOrEditButton.text = "Edit"
            addOrEditButton.setOnClickListener {
                viewModel.editTask(taskId, titleInput.text.toString(), 0)
                dismiss()
            }
        }

    }
    private fun initEmpty() {
        deleteIcon.hide()
        addOrEditButton.text = "Add"
        addOrEditButton.setOnClickListener {
            viewModel.addNewTask(titleInput.text.toString(), 0)
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onAddOrEdit()
    }
}
