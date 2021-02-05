package com.fetecom.pomodoro.ui.timer

import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.os.Build
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import android.view.animation.BounceInterpolator
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import com.fetecom.pomodoro.R
import kotlinx.android.parcel.Parcelize

class TimerView : View {
    companion object {
        const val START_TIMER_ANIMATION_DURATION = 600L
        const val START_ANGLE = 270f
    }

    private var bgColor: Int = 0
    private var fgColor: Int = 0
    private var fgColorWhenPause: Int = 0
    private var statusIconColor: Int = 0
    private lateinit var bgPaint: Paint
    private lateinit var fgPaint: Paint
    private lateinit var statusIconPaint: Paint

    private lateinit var fgOval: RectF
    private lateinit var bgOval: RectF
    private lateinit var statusIcon: Rect

    private var percent: Float = 0.toFloat()

    private var elevationPlay: Float = 0.toFloat()
    private var elevationPause: Float = 0.toFloat()

    private var playIcon: Drawable? = null
    private var pauseIcon: Drawable? = null

    private var showPlayIcon: Boolean = true
    private var showPauseIcon: Boolean = false

    private var strokeWidth: Float = 0.toFloat()
    private var circlePadding: Float = 0.toFloat()

    val rington = RingtoneManager.getRingtone(context,
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val a = context.theme.obtainStyledAttributes(attrs,
            R.styleable.TimerViewStyleable,
            0, 0)

        try {
            bgColor = a.getColor(R.styleable.TimerViewStyleable_bgColor, 0)
            fgColor = a.getColor(R.styleable.TimerViewStyleable_fgColor, 0)
            fgColorWhenPause = a.getColor(R.styleable.TimerViewStyleable_fgColorPause, 0)
            percent = a.getFloat(R.styleable.TimerViewStyleable_percent, 0f)
            elevationPause = resources.dipToPx(a.getFloat(R.styleable.TimerViewStyleable_elevationPause, 0f))
            elevationPlay = resources.dipToPx(a.getFloat(R.styleable.TimerViewStyleable_elevationPlay, 0f))
            playIcon = AppCompatResources.getDrawable(context, R.drawable.ic_play)
            pauseIcon = AppCompatResources.getDrawable(context, R.drawable.ic_pause)
            strokeWidth = a.getFloat(R.styleable.TimerViewStyleable_timerStrokeWidth, 0f)
            circlePadding = a.getFloat(R.styleable.TimerViewStyleable_circlePadding, 0f)
        } finally {
            a.recycle()
        }
        init()
    }

    private fun init() {
        bgPaint = Paint()
        bgPaint.color = bgColor
        bgPaint.isAntiAlias = true

        fgPaint = Paint()
        fgPaint.color = fgColorWhenPause
        fgPaint.isAntiAlias = true

        statusIconPaint = Paint()
        statusIconColor = Color.RED
        statusIconPaint.color = statusIconColor
        statusIconPaint.isAntiAlias = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val ovalPadding = (w * circlePadding)
        val centerX = (w / 2).toFloat()
        val centerY = (h / 2).toFloat()
        val centerIconWidth = (w / 2)

        fgOval = RectF(strokeWidth + ovalPadding, strokeWidth + ovalPadding,
            w - strokeWidth - ovalPadding, h - strokeWidth - ovalPadding)
        bgOval = RectF(strokeWidth, strokeWidth,
            w - strokeWidth, h - strokeWidth)

        statusIcon = Rect(
            (centerX - centerIconWidth / 2).toInt(),
            (centerY - centerIconWidth / 2).toInt(),
            (centerX + centerIconWidth / 2).toInt(),
            (centerY + centerIconWidth / 2).toInt()
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.outlineProvider = OutlineProvider(resources = resources)
            this.elevation = elevationPause
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawTimer(canvas)
        if (showPlayIcon)
            drawPlay(canvas)
        if (showPauseIcon)
            drawPause(canvas)
    }

    private fun drawTimer(canvas: Canvas) {
        canvas.drawArc(bgOval, START_ANGLE, 360f, true, bgPaint)
        canvas.drawArc(fgOval, START_ANGLE, percent * 3.6f, true, fgPaint)
    }

    private fun drawPlay(canvas: Canvas) {
        playIcon?.bounds = statusIcon
        playIcon?.draw(canvas)
    }

    private fun drawPause(canvas: Canvas) {
        pauseIcon?.bounds = statusIcon
        pauseIcon?.draw(canvas)
    }

    fun start() {
        showPlayIcon = false
        toggle(true)
        animateStarting()
        playSound()
    }

    private fun animateStarting() {
        ValueAnimator.ofFloat(0f, 100f).apply {
            duration = START_TIMER_ANIMATION_DURATION
            addUpdateListener {
                percent = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    fun finish() {
        showPlayIcon = true
        showPauseIcon = false
        toggle(false)
        playSound()
        animateFinishing()
    }

    private fun animateFinishing() {
        ValueAnimator.ofFloat(percent, 0f).apply {
            duration = 300
            addUpdateListener {
                percent = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    fun play() {
        showPauseIcon = false
        showPlayIcon = false
        toggle(true)
        playSound()
    }

    fun pause() {
        showPauseIcon = true
        showPlayIcon = false
        toggle(false)
    }

    private fun toggle(push: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            elevate(push)
            fadeBackground(push)
        }
    }

    private fun playSound() {
        rington.stop()
        rington.play()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun fadeBackground(play: Boolean) {
        val color: Int = if (!play)
            fgColorWhenPause
        else
            fgColor
        ValueAnimator.ofArgb(fgPaint.color, color).apply {
            duration = 600
            addUpdateListener {
                fgPaint.color = it.animatedValue as Int
                invalidate()
            }
            start()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun elevate(play: Boolean) {
        val elevateTo: Float = if (play)
            elevationPlay
        else
            elevationPause

        ValueAnimator.ofFloat(elevation, elevateTo).apply {
            duration = if (play)
                600
            else
                300

            addUpdateListener {
                elevation = it.animatedValue as Float
            }
            if (play)
                interpolator = BounceInterpolator()
            start()
        }
    }

    fun updateProgress(currentProgress: Int) {
        if (percent < currentProgress)
            return
        ValueAnimator.ofFloat(percent, currentProgress.toFloat()).apply {
            duration = 300
            addUpdateListener {
                percent = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    fun getViewState(): TimerViewState {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TimerViewState(percent, showPauseIcon, showPlayIcon, elevation, fgPaint.color)
        } else {
            TimerViewState(percent, showPauseIcon, showPlayIcon, fgColor = fgPaint.color)
        }
    }

    fun setViewState(timerViewState: TimerViewState) {
        percent = timerViewState.percent
        showPauseIcon = timerViewState.showPause
        showPlayIcon = timerViewState.showPlay
        fgPaint.color = timerViewState.fgColor
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            elevation = timerViewState.elevation
        }
    }

    fun Resources.dipToPx(dip: Float) : Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dip, this.displayMetrics)
    }


    @Parcelize
    data class TimerViewState(val percent: Float, val showPause: Boolean, val showPlay: Boolean,
                              val elevation: Float = 0f, val fgColor: Int) : Parcelable

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    class OutlineProvider(val resources: Resources) : ViewOutlineProvider() {

        private val rect: Rect = Rect()

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun getOutline(view: View?, outline: Outline?) {
            view?.background?.copyBounds(rect)
            outline?.setRoundRect(rect, resources.getDimensionPixelSize(R.dimen.timer_radius_corner).toFloat())
        }
    }
}