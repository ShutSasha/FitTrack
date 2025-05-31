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

class NutritionProgressView @JvmOverloads constructor(
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

    private var progress = 0f
    private var consumed = 0
    private var goal = 0
    private var label = ""
    private var unit = "g"

    private var animator: ValueAnimator? = null

    fun setNutrition(consumed: Int, goal: Int, label: String, unit: String) {
        this.consumed = consumed
        this.goal = goal
        this.label = label
        this.unit = unit

        val targetProgress = if (goal > 0) {
            (consumed.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

        animator?.cancel()
        animator = ValueAnimator.ofFloat(progress, targetProgress).apply {
            duration = 800
            addUpdateListener {
                progress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val size = min(width.toFloat(), height - dpToPx(24f))
        val radius = size / 2f - strokeWidthPx / 2f
        val cx = width / 2f
        val cy = (height - dpToPx(24f)) / 2f

        val rect = RectF(cx - radius, cy - radius, cx + radius, cy + radius)

        canvas.drawArc(rect, 0f, 360f, false, backgroundPaint)
        canvas.drawArc(rect, -90f, 360f * progress, false, progressPaint)

        val centerText = "$consumed/$goal $unit"
        textPaint.textSize = spToPx(16f)
        textPaint.typeface = ResourcesCompat.getFont(context, R.font.sf_pro_text_regular)
        canvas.drawText(centerText, cx, cy + dpToPx(6f), textPaint)

        textPaint.textSize = spToPx(16f)
        canvas.drawText(label, cx, height.toFloat() - dpToPx(2f), textPaint)
    }

    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    private fun spToPx(sp: Float): Float {
        return sp * resources.displayMetrics.scaledDensity
    }
}
