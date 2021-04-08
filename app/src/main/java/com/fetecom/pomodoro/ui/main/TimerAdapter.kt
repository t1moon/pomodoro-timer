package com.fetecom.pomodoro.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fetecom.pomodoro.R
import com.fetecom.pomodoro.common.ListAdapterItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.tasks_fragment_task_item_timer_item.*
import kotlin.math.max
import kotlin.math.min

class TimerAdapter : ListAdapter<ListAdapterItem, RecyclerView.ViewHolder>(ItemDiff()) {
    companion object {
        const val TIMER = 0
        const val MAX_TIMERS_IN_LIST = 10
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).getType()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TIMER -> TimerHolder(LayoutInflater.from(parent.context).inflate(R.layout.tasks_fragment_task_item_timer_item, parent, false))
            else -> throw IllegalArgumentException("Wrong viewholder cardType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TIMER -> {
                holder as TimerHolder
                holder.bind(getItem(position) as TimerModel)
            }
        }
    }

    fun submitList(estimation: Int, done: Int) {
        val estimationOrDone = max(estimation, done)
        submitList((1..min(estimationOrDone, MAX_TIMERS_IN_LIST)).map {
            TimerModel(it <= done)
        })
    }


    inner class TimerHolder(override val containerView: View)
        : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(item: TimerModel) {
            val timerRes = if (item.isEnabled)
                R.drawable.ic_countdown
            else
                R.drawable.ic_countdown

            timerIcon.setBackgroundResource(timerRes)
        }
    }
    class TimerModel(val isEnabled: Boolean) : ListAdapterItem() {
        override fun getType() = TIMER
        override fun getId() = isEnabled.hashCode()
    }

    class ItemDiff : DiffUtil.ItemCallback<ListAdapterItem>() {
        override fun areContentsTheSame(oldItem: ListAdapterItem, newItem: ListAdapterItem): Boolean {
            return (oldItem as TimerModel).equals(newItem as TimerModel)
        }

        override fun areItemsTheSame(oldItem: ListAdapterItem, newItem: ListAdapterItem): Boolean {
            return oldItem.getId() == newItem.getId()
        }
    }

}