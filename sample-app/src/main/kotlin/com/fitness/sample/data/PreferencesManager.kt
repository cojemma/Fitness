package com.fitness.sample.data

import android.content.Context
import android.content.SharedPreferences

enum class CalendarViewType {
    NONE, WEEKLY, MONTHLY
}

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("fitness_sample_prefs", Context.MODE_PRIVATE)

    fun getCalendarViewType(): CalendarViewType {
        val value = prefs.getString(KEY_CALENDAR_VIEW_TYPE, CalendarViewType.NONE.name)
        return try {
            CalendarViewType.valueOf(value ?: CalendarViewType.NONE.name)
        } catch (_: IllegalArgumentException) {
            CalendarViewType.NONE
        }
    }

    fun setCalendarViewType(type: CalendarViewType) {
        prefs.edit().putString(KEY_CALENDAR_VIEW_TYPE, type.name).apply()
    }

    companion object {
        private const val KEY_CALENDAR_VIEW_TYPE = "calendar_view_type"
    }
}
