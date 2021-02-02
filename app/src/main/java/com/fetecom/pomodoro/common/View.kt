package com.fetecom.pomodoro.common

import android.view.View

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide(keepSpace: Boolean = false) {
    visibility = if (keepSpace) View.INVISIBLE else View.GONE
}
fun View.setVisible(visible: Boolean, keepSpace: Boolean = false) = if (visible) show() else hide(keepSpace)
