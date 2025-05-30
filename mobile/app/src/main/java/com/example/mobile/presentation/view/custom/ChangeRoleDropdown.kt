package com.example.mobile.presentation.view.custom

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.mobile.R

class ChangeRoleDropdown(
    private val isMainDropdown : Boolean = false,
    private val context: Context,
    private val container: LinearLayout,
    private val header: View,
    private val arrow: ImageView,
    private val selectedTextView: TextView,
    private val options: List<Pair<String, String>>,
    private val onOptionSelected: (String) -> Unit
) {
    private var isDropdownVisible = false

    init {
        header.setOnClickListener {
            toggleDropdown()
        }
        arrow.setOnClickListener {
            toggleDropdown()
        }

        populateDropdownOptions()
    }

    private fun toggleDropdown() {
        container.visibility = if (isDropdownVisible) View.GONE else View.VISIBLE
        arrow.rotation = if (isDropdownVisible) 0f else 270f
        isDropdownVisible = !isDropdownVisible
    }

    private fun populateDropdownOptions() {

        container.removeAllViews()

        for ((label, key) in options) {
            val optionView = TextView(context).apply {
                text = label
                textSize = 16f
                setTextColor(Color.BLACK)
                setPadding(24, 0, 24, 0)
                if(isMainDropdown){
                    setBackgroundResource(R.drawable.shape_outlined_button)
                } else {
                    setBackgroundResource(R.drawable.shape_outlined_dropdown_item)
                }
                gravity = Gravity.CENTER_VERTICAL
                isClickable = true
                isFocusable = true
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(51)
                ).apply {
                    setMargins(0, 16, 0, 0)
                }
                setOnClickListener {
                    container.visibility = View.GONE
                    arrow.rotation = 0f
                    isDropdownVisible = false
                    onOptionSelected(key)
                }
            }
            container.addView(optionView)
        }

    }

    private fun dpToPx(dp: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
}