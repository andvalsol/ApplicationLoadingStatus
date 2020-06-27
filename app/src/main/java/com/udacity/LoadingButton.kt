package com.udacity

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.content_main.*
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var _backgroundColor: Int = Color.BLACK // By default use a black color
    private var _textColor: Int = Color.BLACK // By default use a black color

    private var valueAnimator: ValueAnimator

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->
    }

    private val updateListener = ValueAnimator.AnimatorUpdateListener {
        progress = (it.animatedValue as Float).toDouble()

        invalidate()
        requestLayout()
    }

    fun hasCompletedDownload() {
        // cancel the animation
        valueAnimator.cancel()

        buttonState = ButtonState.Completed
        invalidate()
        requestLayout()
    }

    @Volatile
    private var progress: Double = 0.0

    init {
        isClickable = true

        // Initialize the value animator
        valueAnimator = AnimatorInflater.loadAnimator(
            context, R.animator.loading_button
        ) as ValueAnimator

        valueAnimator.addUpdateListener(updateListener)

        val attributes = context.theme.obtainStyledAttributes(attrs,
            R.styleable.LoadingButton,
            0,
            0)

        try {
            _backgroundColor = attributes.getColor(R.styleable.LoadingButton_customBackgroundColor,
                ContextCompat.getColor(context, R.color.colorPrimary))

            _textColor = attributes.getColor(R.styleable.LoadingButton_customTextColor,
            ContextCompat.getColor(context, R.color.colorPrimary))
        } finally {
            attributes.recycle()
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    override fun performClick(): Boolean {
        super.performClick()
        if (buttonState == ButtonState.Completed) buttonState = ButtonState.Loading
        animateButton()

        return true
    }

    private fun animateButton() {
        valueAnimator.start()
    }

    private val rect = RectF(
        10f,
        10f,
        60f,
        60f
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.strokeWidth = 0f
        paint.color = _backgroundColor
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        if (buttonState == ButtonState.Loading) {
            paint.color = Color.CYAN
            canvas.drawRect(
                0f, 0f,
                (width * (progress / 100)).toFloat(), height.toFloat(), paint
            )

            paint.color = Color.YELLOW

            canvas.drawArc(rect, 0f, (360 * (progress / 100)).toFloat(), false, paint)
        }

        val label =
            if (buttonState == ButtonState.Loading)
                resources.getString(R.string.loading)
            else resources.getString(R.string.download)

        paint.color = _textColor
        canvas.drawText(label, (width / 2).toFloat(), ((height + 30) / 2).toFloat(), paint)
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
}