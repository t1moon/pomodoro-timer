package com.fetecom.pomodoro.ui.timer

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.fetecom.pomodoro.R
import kotlinx.android.synthetic.main.timer_fragment.*

class TimerFragment : Fragment(R.layout.timer_fragment) {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initScreen()
    }

    private fun initScreen() {
        timerView.start()
    }
}