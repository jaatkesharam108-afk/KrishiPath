package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.model.AppLanguage
import com.example.domain.model.StudentProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "krishipath_prefs")

class PreferencesManager(private val context: Context) {

    private object Keys {
        val LANGUAGE = stringPreferencesKey("app_language")
        val STUDENT_NAME = stringPreferencesKey("student_name")
        val TARGET_EXAM = stringPreferencesKey("target_exam")
        val DAILY_GOAL_MINUTES = intPreferencesKey("daily_goal_minutes")
    }

    val profileFlow: Flow<StudentProfile> = context.dataStore.data.map { prefs ->
        val langCode = prefs[Keys.LANGUAGE] ?: AppLanguage.HINDI.code
        val language = AppLanguage.entries.firstOrNull { it.code == langCode } ?: AppLanguage.HINDI
        StudentProfile(
            name = prefs[Keys.STUDENT_NAME] ?: "कृषि विद्यार्थी",
            targetExam = prefs[Keys.TARGET_EXAM] ?: "ICAR AIEEA / CUET-UG",
            language = language,
            dailyGoalMinutes = prefs[Keys.DAILY_GOAL_MINUTES] ?: 45
        )
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LANGUAGE] = language.code
        }
    }

    suspend fun updateProfile(name: String, targetExam: String, dailyGoalMinutes: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.STUDENT_NAME] = name.trim().ifEmpty { "कृषि विद्यार्थी" }
            prefs[Keys.TARGET_EXAM] = targetExam.trim().ifEmpty { "ICAR AIEEA / CUET-UG" }
            prefs[Keys.DAILY_GOAL_MINUTES] = dailyGoalMinutes.coerceIn(15, 240)
        }
    }
}
