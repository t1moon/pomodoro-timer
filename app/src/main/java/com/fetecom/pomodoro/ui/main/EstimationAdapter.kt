package com.fetecom.pomodoro.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fetecom.pomodoro.R
import com.fetecom.pomodoro.common.ListAdapterItem
import com.fetecom.pomodoro.common.colorThemed
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.tasks_fragment_add_task_dialog_estimation_item.*

class EstimationAdapter : ListAdapter<ListAdapterItem, RecyclerView.ViewHolder>(ItemDiff()) {
    val interactor: Interactor

    var chosenNumber = 1

    init {
        interactor = object : Interactor {
            override fun onItemChosen(number: Int) {
                chosenNumber = number
                updateListWithChosenNumber(number)
            }
        }
    }

    fun updateListWithChosenNumber(number: Int) {
        submitList((1..ESTIMATION_MAX_NUMBER).map { index ->
            NumberModel(index, index == number)
        })
    }

    companion object {
        const val NUMBER_TYPE = 0
        const val ESTIMATION_MAX_NUMBER = 6
        const val ESTIMATION_DEFAULT_NUMBER = 1
    }

    interface Interactor {
        fun onItemChosen(number: Int)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).getType()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            NUMBER_TYPE -> NumberHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.tasks_fragment_add_task_dialog_estimation_item, parent, false)
            )
            else -> throw IllegalArgumentException("Wrong viewholder cardType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            NUMBER_TYPE -> {
                holder as NumberHolder
                holder.bind(getItem(position) as NumberModel)
            }
        }
    }

    inner class NumberHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(item: NumberModel) {
            if (item.chosen) {
                backgroundColor.setBackgroundResource(R.drawable.estimation_bg_active)
                estimationNumber.setTextColor(colorThemed(R.attr.colorOnPrimary))
            } else {
                backgroundColor.setBackgroundResource(R.drawable.estimation_bg_inactive)
                estimationNumber.setTextColor(colorThemed(R.attr.colorOnSurface))
            }
            estimationNumber.text = item.number.toString()
            itemView.setOnClickListener {
                if (!item.chosen)
                    interactor.onItemChosen(item.number)
            }
        }
    }

    data class NumberModel(
        val number: Int,
        val chosen: Boolean = false
    ) : ListAdapterItem() {
        override fun getType() = NUMBER_TYPE
        override fun getId() = number
    }

    class ItemDiff : DiffUtil.ItemCallback<ListAdapterItem>() {
        override fun areContentsTheSame(
            oldItem: ListAdapterItem,
            newItem: ListAdapterItem
        ): Boolean {
            return (oldItem as NumberModel).equals(newItem as NumberModel)
        }

        override fun areItemsTheSame(oldItem: ListAdapterItem, newItem: ListAdapterItem): Boolean {
            return oldItem.getId() == newItem.getId()
        }
    }
}