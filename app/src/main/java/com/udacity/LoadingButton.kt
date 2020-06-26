package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->

    }

    @Volatile
    private var progress: Double = 0.0

    init {
        isClickable = true
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


        return true
    }

    fun animateButton() {
        Thread(Runnable {
            var i = 0
            while (i < 101) {

                Thread.sleep(10)

                this.invalidate()

                progress = i.toDouble()

                if (progress == 100.0) buttonState = ButtonState.Completed

                i++
            }
        }).start()
    }

    private val rect = RectF(
        50f,
        50f,
        100f,
        100f
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.strokeWidth = 0f
        paint.color = Color.DKGRAY
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

        paint.color = Color.WHITE
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