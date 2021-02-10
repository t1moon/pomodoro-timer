package com.fetecom.pomodoro.ui.main

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.fetecom.domain.Task
import com.fetecom.pomodoro.R
import com.fetecom.pomodoro.common.hide
import com.fetecom.pomodoro.common.show
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.tasks_fragment_add_task_dialog.*
import kotlinx.android.synthetic.main.tasks_fragment_add_task_dialog_estimation_item.*
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
                taskId?.let { putInt(TASK_ID, taskId) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context)
            .inflate(R.layout.tasks_fragment_add_task_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        viewModel.editableTask.value?.let {
            initTask(it)
        } ?: initEmpty()
        titleInput.requestFocus()
    }


    private fun initTask(task: Task) {
        titleInput.setText(task.title)
        deleteIcon.show()
        addOrEditButton.text = "Edit"
        addOrEditButton.setOnClickListener {
            viewModel.editTask(
                task.id,
                titleInput.text.toString(),
                0
            )
            dismiss()
        }
        deleteIcon.setOnClickListener {
            viewModel.deleteEditableTask()
            dismiss()
        }
        val checkedChipId = chipGroup.checkedChipId
        initEstimationChips(checkedChipId)
    }

    private fun initEmpty() {
        deleteIcon.hide()
        addOrEditButton.text = "Add"

        addOrEditButton.setOnClickListener {
            viewModel.addNewTask(
                title = titleInput.text.toString(),
                estimation = 0
            )
            dismiss()
        }
        initEstimationChips()
    }

    private fun initEstimationChips(checkedChipId: Int = 1) {
        for (i in 1..6) {
            chipGroup.addView(Chip(requireContext()).apply {
                text = "$i"
                isChecked = i == checkedChipId
            })
        }
        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            viewModel.estimationOfAddingTask.value = checkedId
        }
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onAddOrEdit()
    }
    lateinit var modalBottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    override fun onStart() {
        super.onStart()
        modalBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        modalBottomSheetBehavior = (dialog as BottomSheetDialog).behavior
        return dialog
    }


}
