package com.fetecom.pomodoro.ui.timer

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.util.AttributeSet
import android.view.View
import android.view.animation.BounceInterpolator
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.ImageViewCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.fetecom.pomodoro.R

class PomoTimer(context: Context, attrs: AttributeSet) : View(context, attrs), View.OnClickListener {
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
    private var progressInPercent: Float = 100f
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
                playIcon?.colorFilter = PorterDuffColorFilter(statusIconColor, PorterDuff.Mode.SRC_ATOP)
                pauseIcon?.colorFilter = PorterDuffColorFilter(statusIconColor, PorterDuff.Mode.SRC_ATOP)
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
            RectF(0f + innerCirclePadding, 0f + innerCirclePadding,
                w.toFloat() - innerCirclePadding, h.toFloat() - innerCirclePadding)

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
            progressInPercent * PROGRESS_MULTIPLIER,
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


    private var stopped = true
    private var playing = false
    fun setPlayState(withAnimation: Boolean = true) {
        stopped = false
        playing = true
        hideIcon()
        toggle(true)
        if (withAnimation)
            animateStarting()
        playSound()
    }

    fun setPauseState() {
        playing = false
        showPauseIcon()
        toggle(false)
    }

    fun setFinishState(withAnimation: Boolean = true) {
        showPlayIcon()
        toggle(false)
        playSound()
        if (withAnimation)
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
                progressInPercent = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun animateFinishing() {
        ValueAnimator.ofFloat(progressInPercent, 0f).apply {
            duration = ANIMATION_TIMER_FINISH
            addUpdateListener {
                progressInPercent = it.animatedValue as Float
                invalidate()
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

    fun updateProgress(newProgress: Int) {
        if (progressInPercent < newProgress)
            return
        ValueAnimator.ofFloat(progressInPercent, newProgress.toFloat()).apply {
            duration = UPDATE_PROGRESS_DURATION
            addUpdateListener {
                progressInPercent = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onClick(v: View?) {
        if (!playing)
            setPlayState(withAnimation = stopped)
        else
            setPauseState()
    }

    fun releaseResources() {
        soundCustom.release()
    }

}