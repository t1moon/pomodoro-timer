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
import com.fetecom.pomodoro.common.setVisible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.tasks_fragment_date_item.*
import org.joda.time.LocalDate

class DatesAdapter(
    private val outerInteractor: OuterInteractor
) : ListAdapter<ListAdapterItem, RecyclerView.ViewHolder>(ItemDiff()) {
    val innerInteractor: Interactor

    private val listOfDatesThisWeek = listOf(
        LocalDate.now().withDayOfWeek(1),
        LocalDate.now().withDayOfWeek(2),
        LocalDate.now().withDayOfWeek(3),
        LocalDate.now().withDayOfWeek(4),
        LocalDate.now().withDayOfWeek(5),
        LocalDate.now().withDayOfWeek(6),
        LocalDate.now().withDayOfWeek(7)
    )

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
            dot.setVisible(item.chosen)
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