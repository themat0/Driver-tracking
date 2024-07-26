package com.example.drivertracking.utils

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class DataStoreManager(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val MAX_FPS = intPreferencesKey("max_fps")
        val SENSITIVITY = intPreferencesKey("sensitivity")
        val IS_MAX_FPS_ENABLED = booleanPreferencesKey("is_max_fps_enabled")
    }

    val maxFPS: Flow<Int> = dataStore.data.map { preferences ->
        preferences[MAX_FPS] ?: 24
    }

    val sensitivity: Flow<Int> = dataStore.data.map { preferences ->
        preferences[SENSITIVITY] ?: 5
    }

    val isMaxFPSEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_MAX_FPS_ENABLED] ?: true
    }

    suspend fun setMaxFPS(value: Int) {
        dataStore.edit { preferences ->
            preferences[MAX_FPS] = value
        }
    }

    suspend fun setSensitivity(value: Int) {
        dataStore.edit { preferences ->
            preferences[SENSITIVITY] = value
        }
    }

    suspend fun setIsMaxFPSEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_MAX_FPS_ENABLED] = enabled
        }
    }
}
