package com.example.mobile.data.dto.dailyLog

import com.example.mobile.domain.model.LoggedActivity
import com.example.mobile.domain.model.Meal
import com.example.mobile.domain.model.Nutrition

data class UserDailyLogRes(
    val _id: String,
    val userId: String,
    val date: String,
    val burnedCalories: Int,
    val totalCalories: Double,
    val meals: List<Meal>,
    val calories: Nutrition,
    val protein: Nutrition,
    val fat: Nutrition,
    val carbs: Nutrition,
    val water: Nutrition,
    val weight: Nutrition,
    val activities: List<LoggedActivity>,
)
