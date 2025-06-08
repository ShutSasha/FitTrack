package com.example.mobile.presentation.view.custom

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.mobile.R

class FilterDropdown(private val context: Context) {

    private var isDropdownVisible = false
    private var selectedKey: String? = null

    fun setUpDropdown(
        container: LinearLayout,
        header: View,
        arrow: ImageView,
        selectedTextView: TextView,
        options: List<Pair<String, String>>,
        initialSelectedKey: String? = null,
        onOptionSelected: (String?) -> Unit
    ) {
        if (initialSelectedKey != null) {
            val initialLabel = options.find { it.second == initialSelectedKey }?.first
            selectedTextView.text = initialLabel ?: ""
            selectedKey = initialSelectedKey
        }

        header.setOnClickListener {
            toggleDropdown(container, arrow)
        }

        arrow.setOnClickListener {
            toggleDropdown(container, arrow)
        }

        populateDropdownOptions(container, selectedTextView, arrow, options, onOptionSelected)
    }

    private fun toggleDropdown(container: LinearLayout, arrow: ImageView) {
        container.visibility = if (isDropdownVisible) View.GONE else View.VISIBLE
        arrow.rotation = if (isDropdownVisible) 0f else 270f
        isDropdownVisible = !isDropdownVisible
    }

    private fun populateDropdownOptions(
        container: LinearLayout,
        selectedTextView: TextView,
        arrow: ImageView,
        options: List<Pair<String, String>>,
        onOptionSelected: (String?) -> Unit,
        initialSelectedKey: String? = null,
    ) {
        container.removeAllViews()
        for ((label, key) in options) {
            val optionView = TextView(context).apply {
                text = label
                textSize = 16f
                setTextColor(Color.BLACK)
                setPadding(24, 0, 24, 0)
                setBackgroundResource(R.drawable.shape_outlined_button)
                gravity = Gravity.CENTER_VERTICAL
                isClickable = true
                isFocusable = true

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(51)
                ).apply {
                    setMargins(0, 12, 0, 0)
                }

                if (selectedKey == null && initialSelectedKey == key) {
                    selectedKey = key
                    selectedTextView.text = label
                }

                setOnClickListener {
                    if (selectedKey == key) {
                        selectedKey = null
                        selectedTextView.text = ""
                        onOptionSelected(null)
                    } else {
                        selectedKey = key
                        selectedTextView.text = label
                        onOptionSelected(key)
                    }
                    container.visibility = View.GONE
                    arrow.rotation = 0f
                    isDropdownVisible = false
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
