package com.fetecom.pomodoro.ui.main

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.NonNull
import androidx.core.view.ViewCompat
import com.fetecom.domain.Task
import com.fetecom.pomodoro.R
import com.fetecom.pomodoro.common.hide
import com.fetecom.pomodoro.common.show
import com.fetecom.pomodoro.observe
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
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
            .inflate(R.layout.tasks_fragment_task_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        observe(viewModel.editableTask) {
            initTask(it)
        }
        initEmpty()
        titleInput.requestFocus()
    }

    private fun initTask(task: Task) {
        titleInput.setText(task.title)
        deleteIcon.show()
        addOrEditButton.text = "Edit"
        estimationBtn.setOnClickListener {
            showEstimationPicker()
        }
        estimationBtn.text = task.estimation.toString()
        addOrEditButton.setOnClickListener {
            viewModel.editTask(
                task.id,
                titleInput.text.toString(),
                estimationBtn.toString().toInt()
            )
            dismiss()
        }
        deleteIcon.setOnClickListener {
            viewModel.deleteEditableTask()
            dismiss()
        }
    }

    private fun initEmpty() {
        deleteIcon.hide()
        addOrEditButton.text = "Add"
        estimationBtn.setOnClickListener {
            showEstimationPicker()
        }
        addOrEditButton.setOnClickListener {
            viewModel.addNewTask(
                title = titleInput.text.toString(),
                estimation = estimationBtn.text.toString().toInt()
            )
            dismiss()
        }
    }

    private fun showEstimationPicker() {
        val items = arrayOf("1", "2", "3")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.estimation_dialog_title))
            .setItems(items) { dialog, which ->
                estimationBtn.text = items[which]
            }
            .show()
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
