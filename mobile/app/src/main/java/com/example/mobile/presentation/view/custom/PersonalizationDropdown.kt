package com.example.mobile.presentation.view.util

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.mobile.R

class PersonalizationDropdown(private val context: Context) {

    private val dropdownStates = mutableMapOf<LinearLayout, Boolean>()

    fun setUpDropdown(
        container: LinearLayout,
        header: View,
        arrow: ImageView,
        selectedTextView: TextView,
        options: List<Pair<String, String>>,
        onOptionSelected: (String) -> Unit,
        optionBackgroundRes: Int = R.drawable.shape_outlined_button
    ) {
        dropdownStates[container] = false

        header.setOnClickListener { toggleDropdown(container, arrow) }
        arrow.setOnClickListener { toggleDropdown(container, arrow) }

        populateDropdownOptions(
            container,
            selectedTextView,
            arrow,
            options,
            onOptionSelected,
            optionBackgroundRes
        )
    }

    private fun toggleDropdown(container: LinearLayout, arrow: ImageView) {
        val isVisible = dropdownStates[container] ?: false
        container.visibility = if (isVisible) View.GONE else View.VISIBLE
        arrow.rotation = if (isVisible) 0f else 270f
        dropdownStates[container] = !isVisible
    }

    private fun populateDropdownOptions(
        container: LinearLayout,
        selectedTextView: TextView,
        arrow: ImageView,
        options: List<Pair<String, String>>,
        onOptionSelected: (String) -> Unit,
        optionBackgroundRes: Int
    ) {
        container.removeAllViews()
        for ((label, key) in options) {
            val optionView = TextView(context).apply {
                text = label
                textSize = 16f
                setTextColor(Color.BLACK)
                setPadding(24, 0, 24, 0)
                setBackgroundResource(optionBackgroundRes)
                gravity = Gravity.CENTER_VERTICAL
                isClickable = true
                isFocusable = true

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(51)
                ).apply {
                    setMargins(0, 12, 0, 0)
                }

                setOnClickListener {
                    selectedTextView.text = label
                    container.visibility = View.GONE
                    arrow.rotation = 0f
                    dropdownStates[container] = false
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
