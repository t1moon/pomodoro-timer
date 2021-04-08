package com.fetecom.pomodoro.ui.main.date

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fetecom.data.isToday
import com.fetecom.pomodoro.R
import com.fetecom.pomodoro.common.ListAdapterItem
import com.fetecom.pomodoro.common.colorThemed
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.tasks_fragment_date_item.*
import org.joda.time.LocalDate

class DatesAdapter(
    private val outerInteractor: OuterInteractor
) : ListAdapter<ListAdapterItem, RecyclerView.ViewHolder>(ItemDiff()) {
    val innerInteractor: Interactor

    private val listOfDatesThisWeek = (1..60).fold(mutableListOf(LocalDate.now()), { dateList, dayIndexToAdd: Int ->
        dateList.add(LocalDate.now().plusDays(dayIndexToAdd))
        dateList
    })

    init {
        innerInteractor = object : Interactor {
            override fun onItemChosen(index: Int) {
                updateListWithChosenNumber(index)
            }
        }

    }

    fun updateListWithChosenNumber(index: Int? = null) {
        submitList(listOfDatesThisWeek.mapIndexed { _index, date ->
            DateModel(
                date = date,
                index = _index,
                chosen = if (index == null)
                    date.isToday()
                else
                    _index == index
            )
        })
    }

    companion object {
        const val DATE_TYPE = 1858
    }

    interface Interactor {
        fun onItemChosen(index: Int)
    }
    interface OuterInteractor {
        fun onDateChosen(date: LocalDate)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).getType()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            DATE_TYPE -> DateHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.tasks_fragment_date_item, parent, false)
            )
            else -> throw IllegalArgumentException("Wrong viewholder cardType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            DATE_TYPE -> {
                holder as DateHolder
                holder.bind(getItem(position) as DateModel)
            }
        }
    }

    inner class DateHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(item: DateModel) {
            if (item.chosen) {
                backgroundColor.setBackgroundResource(R.drawable.estimation_bg_active)
                month.setTextColor(colorThemed(R.attr.colorOnPrimary))
                day.setTextColor(colorThemed(R.attr.colorOnPrimary))
                dot.setBackgroundResource(R.drawable.ic_dot_on_primary)
            } else {
                backgroundColor.setBackgroundResource(R.drawable.estimation_bg_inactive)
                month.setTextColor(colorThemed(R.attr.colorOnSurface))
                day.setTextColor(colorThemed(R.attr.colorOnSurface))
                dot.setBackgroundResource(R.drawable.ic_dot)
            }
            month.text = item.date.toString("EEE")
            day.text = item.date.toString("dd")
            itemView.setOnClickListener {
                if (!item.chosen) {
                    innerInteractor.onItemChosen(item.index)
                    outerInteractor.onDateChosen(item.date)
                }
            }
        }
    }

    data class DateModel(
        val date: LocalDate,
        val index: Int,
        val chosen: Boolean = false
    ) : ListAdapterItem() {
        override fun getType() = DATE_TYPE
        override fun getId() = index
    }

    class ItemDiff : DiffUtil.ItemCallback<ListAdapterItem>() {
        override fun areContentsTheSame(
            oldItem: ListAdapterItem,
            newItem: ListAdapterItem
        ): Boolean {
            return (oldItem as DateModel).equals(newItem as DateModel)
        }

        override fun areItemsTheSame(oldItem: ListAdapterItem, newItem: ListAdapterItem): Boolean {
            return oldItem.getId() == newItem.getId()
        }
    }
}