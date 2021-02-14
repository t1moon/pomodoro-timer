package com.fetecom.pomodoro.common

import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide(keepSpace: Boolean = false) {
    visibility = if (keepSpace) View.INVISIBLE else View.GONE
}
fun View.setVisible(visible: Boolean, keepSpace: Boolean = false) = if (visible) show() else hide(keepSpace)

fun RecyclerView.ViewHolder.colorThemed(id: Int) = itemView.context.obtainStyledAttributes(
    TypedValue().data, intArrayOf(id)
).getColor(0,0)
