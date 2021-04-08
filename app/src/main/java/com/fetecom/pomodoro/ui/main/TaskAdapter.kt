package com.fetecom.pomodoro.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fetecom.domain.Task
import com.fetecom.pomodoro.R
import com.fetecom.pomodoro.common.ListAdapterItem
import com.fetecom.pomodoro.common.setVisible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.tasks_fragment_task_item.*

class TaskAdapter(
    val interactor: Interactor
) : ListAdapter<ListAdapterItem, RecyclerView.ViewHolder>(ItemDiff()) {
    companion object {
        const val TASK = 0
    }

    interface Interactor {
        fun onTaskClick(task: Task)
        fun onTaskLongClick(task: Task)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).getType()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TASK -> TaskHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.tasks_fragment_task_item, parent, false)
            )
            else -> throw IllegalArgumentException("Wrong viewholder cardType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TASK -> {
                holder as TaskHolder
                holder.bind(getItem(position) as TaskModel)
            }
        }
    }

    inner class TaskHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(item: TaskModel) {
            with(item.task) {
                taskTitle.text = title
                taskTitle.alpha = if (item.task.isDone) 0.2f else 1f

                itemView.setOnClickListener {
                    interactor.onTaskClick(this)
                }
                itemView.setOnLongClickListener {
                    interactor.onTaskLongClick(this)
                    true
                }
                estimationList.adapter = TimerAdapter().apply {
                    submitList(estimation, completed)
                }
            }

        }
    }

    data class TaskModel(val task: Task) : ListAdapterItem() {
        override fun getType() = TASK
        override fun getId() = task.id
    }

    class ItemDiff : DiffUtil.ItemCallback<ListAdapterItem>() {
        override fun areContentsTheSame(
            oldItem: ListAdapterItem,
            newItem: ListAdapterItem
        ): Boolean {
            return ((oldItem as TaskModel).task).equals((newItem as TaskModel).task)
        }

        override fun areItemsTheSame(oldItem: ListAdapterItem, newItem: ListAdapterItem): Boolean {
            return oldItem.getId() == newItem.getId()
        }
    }
}
