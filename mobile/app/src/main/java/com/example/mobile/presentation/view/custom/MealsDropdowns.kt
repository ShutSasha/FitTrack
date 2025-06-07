package com.example.mobile.presentation.view.custom

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.mobile.R
import com.example.mobile.data.dto.dailyLog.UserDailyLogRes
import com.example.mobile.domain.model.Meal

class MealsDropdowns {

    fun renderMeals(
        dailyLog: UserDailyLogRes,
        dropdownMap: Map<String, View>,
        inflater: LayoutInflater,
        onEditClicked: (String) -> Unit,
        onDeleteClicked: (Meal, String) -> Unit
    ) {
        for ((_, mealView) in dropdownMap) {
            val container = mealView.findViewById<LinearLayout>(R.id.options)
            container.removeAllViews()
            container.visibility = View.GONE
            val arrow = mealView.findViewById<ImageView>(R.id.arrow)
            arrow.rotation = 0f
        }

        for ((mealType, mealView) in dropdownMap) {
            val container = mealView.findViewById<LinearLayout>(R.id.options)
            val header = mealView.findViewById<View>(R.id.header)
            val arrow = mealView.findViewById<ImageView>(R.id.arrow)
            val title = mealView.findViewById<TextView>(R.id.title)
            val totalCalories = mealView.findViewById<TextView>(R.id.totalCalories)

            val meal = dailyLog.meals.find { it.type.equals(mealType, ignoreCase = true) }

            title.text = mealType

            if (meal != null) {
                val options = meal.nutritionProducts.map {
                    Triple(it.productName, it.productCalories.toInt(), it._id)
                }
                val caloriesInt = meal.totalCalories.toInt()
                totalCalories.text = "$caloriesInt Kcal"

                setUpDropdown(
                    container = container,
                    header = header,
                    arrow = arrow,
                    options = options,
                    onEditClicked = onEditClicked,
                    onDeleteClicked = { nutritionEntryId ->
                        onDeleteClicked(meal, nutritionEntryId)
                    }
                )
            } else {
                container.visibility = View.GONE
                arrow.rotation = 0f
                totalCalories.text = "0 Kcal"
            }
        }
    }

    private fun setUpDropdown(
        container: LinearLayout,
        header: View,
        arrow: ImageView,
        options: List<Triple<String, Int, String>>,
        onEditClicked: (String) -> Unit,
        onDeleteClicked: (String) -> Unit
    ) {
        header.setOnClickListener {
            toggleDropdown(container, arrow)
        }
        arrow.setOnClickListener {
            toggleDropdown(container, arrow)
        }
        populateDropdownOptions(container, options, onEditClicked, onDeleteClicked)
    }

    private fun toggleDropdown(container: LinearLayout, arrow: ImageView) {
        val isVisible = container.visibility == View.VISIBLE
        container.visibility = if (isVisible) View.GONE else View.VISIBLE
        arrow.rotation = if (isVisible) 0f else 270f
    }

    private fun populateDropdownOptions(
        container: LinearLayout,
        options: List<Triple<String, Int, String>>,
        onEditClicked: (String) -> Unit,
        onDeleteClicked: (String) -> Unit
    ) {
        container.removeAllViews()
        val inflater = LayoutInflater.from(container.context)

        for ((name, calories, key) in options) {
            val optionView = inflater.inflate(R.layout.dropdown_item_actions, container, false)

            val optionName = optionView.findViewById<TextView>(R.id.optionName)
            val optionCalories = optionView.findViewById<TextView>(R.id.optionCalories)
            val editIcon = optionView.findViewById<ImageView>(R.id.editIcon)
            val deleteIcon = optionView.findViewById<ImageView>(R.id.deleteIcon)

            optionName.text = name
            optionCalories.text = "$calories Kcal"

            editIcon.setOnClickListener {
                onEditClicked(key)
            }

            deleteIcon.setOnClickListener {
                onDeleteClicked(key)
            }

            container.addView(optionView)
        }
    }
}
