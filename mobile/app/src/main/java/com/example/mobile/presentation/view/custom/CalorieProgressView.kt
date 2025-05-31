package com.example.mobile.presentation.view.custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.mobile.R
import kotlin.math.min

class CalorieProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val strokeWidthPx = dpToPx(9f)

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = strokeWidthPx
        color = Color.parseColor("#F3F3F3")
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = strokeWidthPx
        color = Color.parseColor("#007CFF")
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
    }

    private var consumedCalories = 0
    private var goalCalories = 0
    private var unit = "kcal"

    private var animatedProgress = 0f
    private var currentProgress = 0f

    fun setCalories(consumed: Int, goal: Int, unit: String) {
        consumedCalories = consumed
        goalCalories = goal
        this.unit = unit

        val targetProgress = if (goal > 0) {
            (consumed.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
        startAnimation(currentProgress, targetProgress)
        currentProgress = targetProgress
    }

    private fun startAnimation(fromProgress: Float, toProgress: Float) {
        val animator = ValueAnimator.ofFloat(fromProgress, toProgress)
        animator.duration = 1000L
        animator.addUpdateListener {
            animatedProgress = it.animatedValue as Float
            invalidate()
        }
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val size = min(width, height)
        val radius = size / 2f - strokeWidthPx / 2f
        val cx = width / 2f
        val cy = height / 2f

        val rect = RectF(cx - radius, cy - radius, cx + radius, cy + radius)

        canvas.drawArc(rect, 0f, 360f, false, backgroundPaint)

        canvas.drawArc(rect, -90f, 360f * animatedProgress, false, progressPaint)

        val text1 = "$consumedCalories/$goalCalories"
        val text2 = unit

        textPaint.textSize = spToPx(18f)
        textPaint.typeface = ResourcesCompat.getFont(context, R.font.sf_pro_text_regular)
        canvas.drawText(text1, cx, cy - dpToPx(4f), textPaint)

        textPaint.textSize = spToPx(14f)
        canvas.drawText(text2, cx, cy + dpToPx(14f), textPaint)
    }

    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    private fun spToPx(sp: Float): Float {
        return sp * resources.displayMetrics.scaledDensity
    }
}
