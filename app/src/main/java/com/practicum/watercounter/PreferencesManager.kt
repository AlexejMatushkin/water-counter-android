package com.practicum.watercounter

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "water_counter_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_WATER_COUNT = "water_count"
        private const val KEY_DAILY_GOAL = "daily_goal"
        private const val KEY_LAST_UPDATE_DATE = "last_update_date"
    }

    // Сохраняем количество стаканов
    fun saveWaterCount(count: Int) {
        prefs.edit().putInt(KEY_WATER_COUNT, count).apply()
    }

    // Получаем количество стаканов
    fun getWaterCount(): Int {
        return prefs.getInt(KEY_WATER_COUNT, 0)
    }

    // Сохраняем дневную цель
    fun saveDailyGoal(goal: Int) {
        prefs.edit().putInt(KEY_DAILY_GOAL, goal).apply()
    }

    // Получаем дневную цель
    fun getDailyGoal(): Int {
        return prefs.getInt(KEY_DAILY_GOAL, 10)
    }

    // Сохраняем дату последнего обновления
    fun saveLastUpdateDate(date: String) {
        prefs.edit().putString(KEY_LAST_UPDATE_DATE, date).apply()
    }

    // Получаем дату последнего обновления
    fun getLastUpdateDate(): String {
        return prefs.getString(KEY_LAST_UPDATE_DATE, "") ?: ""
    }

    // Сбрасываем счётчик
    fun resetIfNewDay() {
        val today = java.text.SimpleDateFormat(
            "yyyy-MM-dd",
            java.util.Locale.getDefault()
        )
            .format(java.util.Date())

        val lastDate = getLastUpdateDate()

        if (lastDate != today) {
            saveWaterCount(0)
            saveLastUpdateDate(today)
        }
    }
}
