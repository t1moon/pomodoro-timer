package com.fetecom.pomodoro.ui.timer

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.fetecom.data.Reporter
import com.fetecom.pomodoro.R
import com.fetecom.pomodoro.common.ViewModelCustomView
import com.fetecom.pomodoro.observe
import java.lang.IllegalStateException

class PomoTimer(context: Context, attrs: AttributeSet) : View(context, attrs),
    View.OnClickListener, View.OnLongClickListener, ViewModelCustomView, LifecycleObserver {
    override val viewModel = PomoTimerViewModel()

    override fun onLifecycle(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
    }

    override fun onLifecycleOwnerAttached(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.observe(viewModel.newProgress) {
            updateProgress(it)
        }
        lifecycleOwner.observe(viewModel.currentState) {
            when (it) {
                TimerState.INIT -> setFinishState(animate = false, playSound = false)
                TimerState.START -> setPlayState(isStarting = true)
                TimerState.PAUSED -> setPauseState()
                TimerState.PLAY -> setPlayState(isStarting = false)
                TimerState.FINISHED -> setFinishState()
                else -> throw IllegalStateException("This type doesn't exist")
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        viewModel.onInit()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun releaseResources() {
        Reporter.reportD("releaseResources")
        soundCustom.release()
        viewModel.releaseTimer()
    }


    override fun onClick(v: View?) {
        viewModel.onClick()
    }

    override fun onLongClick(v: View?): Boolean {
        viewModel.onLongClick()
        Reporter.reportD("onLongClick")
        return true
    }


    companion object {
        const val START_ANGLE = 270f
        const val PROGRESS_MULTIPLIER = 3.6f

        const val ELEVATION_DURATION_PLAY = 600L
        const val ELEVATION_DURATION_PAUSE = 300L
        const val UPDATE_PROGRESS_DURATION = 300L

        const val FADE_ON_PAUSE_DURATION = 600L
        const val ANIMATION_TIMER_START = 600L
        const val ANIMATION_TIMER_FINISH = 300L

    }

    private val ELEVATION_UP_INTERPOLATOR = FastOutSlowInInterpolator()
    private var elevationOnPlay: Float
    private var elevationOnPause: Float
    private var innerCirclePadding: Float
    private var timerForegroundColor: Int
    private var timerForegroundColorOnPause: Int
    private var statusIconColor: Int

    private lateinit var timerForegroundOval: RectF
    private lateinit var statusIcon: Rect


    private var playIcon: Drawable?
    private var pauseIcon: Drawable?

    // STATE
    private var showingPlayIcon: Boolean = true
    private var showingPauseIcon: Boolean = false

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PomoTimer,
            0, 0
        ).apply {

            try {
                timerForegroundColor = getColor(R.styleable.PomoTimer_timerForegroundColor, 0)
                timerForegroundColorOnPause =
                    getColor(R.styleable.PomoTimer_timerForegroundColorOnPause, 0)
                statusIconColor = getColor(R.styleable.PomoTimer_statusIconColor, 0)
                innerCirclePadding = getDimension(R.styleable.PomoTimer_innerCirclePadding, 0f)
                elevationOnPause = getDimension(R.styleable.PomoTimer_elevationOnPause, 0f)
                elevationOnPlay = getDimension(R.styleable.PomoTimer_elevationOnPlay, 0f)
                elevationOnPlay = getDimension(R.styleable.PomoTimer_elevationOnPlay, 0f)

                playIcon =
                    getDrawable(R.styleable.PomoTimer_playIcon) ?: AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_play
                    )
                pauseIcon =
                    getDrawable(R.styleable.PomoTimer_pauseIcon) ?: AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_pause
                    )
                playIcon?.colorFilter =
                    PorterDuffColorFilter(statusIconColor, PorterDuff.Mode.SRC_ATOP)
                pauseIcon?.colorFilter =
                    PorterDuffColorFilter(statusIconColor, PorterDuff.Mode.SRC_ATOP)
            } finally {
                recycle()
            }
        }
        setOnClickListener(this)
        setBackgroundResource(R.drawable.timer_bg)
        elevation = elevationOnPause
    }

    private val timerForegroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = timerForegroundColor
    }

    private val soundNotification = RingtoneManager.getRingtone(
        context,
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    )
    private val soundCustom = MediaPlayer.create(context, R.raw.timer_sound)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val centerX = (w / 2).toFloat()
        val centerY = (h / 2).toFloat()
        val centerIconWidth = (w / 2)

        timerForegroundOval =
            RectF(
                0f + innerCirclePadding, 0f + innerCirclePadding,
                w.toFloat() - innerCirclePadding, h.toFloat() - innerCirclePadding
            )

        statusIcon = Rect(
            (centerX - centerIconWidth / 2).toInt(),
            (centerY - centerIconWidth / 2).toInt(),
            (centerX + centerIconWidth / 2).toInt(),
            (centerY + centerIconWidth / 2).toInt()
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawTimer(canvas)
        if (showingPlayIcon)
            drawPlay(canvas)
        if (showingPauseIcon)
            drawPause(canvas)
    }


    private fun drawTimer(canvas: Canvas) {
        canvas.drawArc(
            timerForegroundOval,
            START_ANGLE,
            viewModel.currentProgress * PROGRESS_MULTIPLIER,
            true,
            timerForegroundPaint
        )
    }

    private fun drawPlay(canvas: Canvas) {
        playIcon?.bounds = statusIcon
        playIcon?.draw(canvas)
    }

    private fun drawPause(canvas: Canvas) {
        pauseIcon?.bounds = statusIcon
        pauseIcon?.draw(canvas)
    }


    private fun setPlayState(isStarting: Boolean = true) {
        hideIcon()
        toggle(true)
//        if (isStarting)
//            animateStarting()
        playSound()
    }

    private fun setPauseState() {
        showPauseIcon()
        toggle(false)
    }

    private fun setFinishState(animate: Boolean = true, playSound: Boolean = true) {
        showPlayIcon()
        toggle(false)
        if (playSound)
            playSound()
        if (animate)
            animateFinishing()
    }

    private fun hideIcon() {
        showingPauseIcon = false
        showingPlayIcon = false
    }

    private fun showPauseIcon() {
        showingPauseIcon = true
        showingPlayIcon = false
    }

    private fun showPlayIcon() {
        showingPauseIcon = false
        showingPlayIcon = true
    }

    private fun toggle(push: Boolean) {
        elevate(push)
        fadeBackground(push)
    }

    private fun playSound() {
        soundCustom.stop()
        soundCustom.prepare()
        soundCustom.start()
    }

    private fun animateStarting() {
        ValueAnimator.ofFloat(0f, 100f).apply {
            duration = ANIMATION_TIMER_START
            addUpdateListener {
                viewModel.updateProgress(it.animatedValue as Float)
            }
            start()
        }
    }

    private fun animateFinishing() {
        ValueAnimator.ofFloat(viewModel.currentProgress, 0f).apply {
            duration = ANIMATION_TIMER_FINISH
            addUpdateListener {
                viewModel.updateProgress(it.animatedValue as Float)
            }
            start()
        }
    }

    private fun fadeBackground(play: Boolean) {
        val color: Int = if (!play)
            timerForegroundColorOnPause
        else
            timerForegroundColor
        ValueAnimator.ofArgb(timerForegroundPaint.color, color).apply {
            duration = FADE_ON_PAUSE_DURATION
            addUpdateListener {
                timerForegroundPaint.color = it.animatedValue as Int
                invalidate()
            }
            start()
        }
    }

    private fun elevate(play: Boolean) {
        val elevateTo: Float = if (play)
            elevationOnPlay
        else
            elevationOnPause

        ValueAnimator.ofFloat(elevation, elevateTo).apply {
            duration = if (play)
                ELEVATION_DURATION_PLAY
            else
                ELEVATION_DURATION_PAUSE

            addUpdateListener {
                elevation = it.animatedValue as Float
            }
            if (play)
                interpolator = ELEVATION_UP_INTERPOLATOR
            start()
        }
    }

    private fun updateProgress(newProgress: Float) {
//        if (viewModel.currentProgress < newProgress)
//            return

        ValueAnimator.ofFloat(viewModel.currentProgress, newProgress).apply {
            duration = UPDATE_PROGRESS_DURATION
            addUpdateListener {
                viewModel.currentProgress = it.animatedValue as Float
                Reporter.reportD("Current: ${viewModel.currentProgress}")
                invalidate()
            }
            start()
        }
    }
}