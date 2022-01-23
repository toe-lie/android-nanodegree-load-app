package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.udacity.utils.dp
import com.udacity.utils.sp
import kotlin.properties.Delegates

private const val PROPERTY_PROGRESS = "progress"
private const val PROPERTY_DEGREE = "degree"

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var circleSize = 24.dp
    private var progress = 0
    private var degree = 0
    private var text: String = context.resources.getString(R.string.button_text_download)
    private val propertyProgress = PropertyValuesHolder.ofInt(PROPERTY_PROGRESS, 0, 100)
    private val propertyDegree = PropertyValuesHolder.ofInt(PROPERTY_DEGREE, 0, 360)
    private val animator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        if (old == new) return@observable

        val textResId = when (new) {
            is ButtonState.Clicked -> {
                runAnimation()
                R.string.button_text_loading
            }
            is ButtonState.Loading -> {
                R.string.button_text_loading
            }
            is ButtonState.Completed -> {
                progress = 0
                degree = 0
                R.string.button_text_download
            }
        }
        setText(textResId)
        invalidate()
    }

    private fun runAnimation() {
        animator.setValues(propertyProgress, propertyDegree)
        animator.duration = 1500
        animator.addUpdateListener { animation ->
            progress = animation.getAnimatedValue(PROPERTY_PROGRESS) as Int
            degree = animation.getAnimatedValue(PROPERTY_DEGREE) as Int
            invalidate()
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                setState(ButtonState.Completed)
            }
        })
        animator.start()
    }

    private val progressPaint = Paint().apply {
        isDither = true
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val backgroundPaint = Paint().apply {
        isDither = true
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val textPaint = TextPaint().apply {
        isDither = true
        isAntiAlias = true
        textSize = 16.sp
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }

    private val circlePaint = Paint().apply {
        isDither = true
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.YELLOW
    }

    init {
        progressPaint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        backgroundPaint.color = ContextCompat.getColor(context, R.color.colorPrimary)
        textPaint.color = ContextCompat.getColor(context, R.color.white)
    }

    override fun onDraw(canvas: Canvas) {
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        val textX = viewWidth / 2
        val textY = (viewHeight / 2 - (textPaint.descent() + textPaint.ascent()) / 2)

        canvas.drawRect(0f, 0f, viewWidth, viewHeight, backgroundPaint)

        if (progress > 0) {
            val progressWidth = (viewWidth * progress) / 100
            canvas.drawRect(0f, 0f, progressWidth, viewHeight, progressPaint)
        }

        if (degree > 0) {
            val textLength = textPaint.measureText(text)
            val circleLeft = textX + (textLength / 2) + 4.dp
            val circleRight = circleLeft + circleSize
            val circleTop = textY - (circleSize / 2)
            val circleBottom = textY + (circleSize / 2)
            canvas.drawArc(circleLeft, circleTop, circleRight, circleBottom, 0f, degree.toFloat(), true, circlePaint)
        }

        canvas.drawText(text, textX, textY, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun setText(@StringRes text: Int) {
        setText(resources.getString(text))
    }

    private fun setText(text: String) {
        this.text = text
    }

    fun setState(state: ButtonState) {
        buttonState = state
    }
}