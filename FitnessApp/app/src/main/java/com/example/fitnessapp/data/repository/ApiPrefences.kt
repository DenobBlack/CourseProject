package com.example.fitnessapp.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object ApiPreferences {
    private val Context.dataStore by preferencesDataStore("settings")

    val API_URL = stringPreferencesKey("api_url")

    suspend fun saveApiUrl(context: Context, url: String) {
        context.dataStore.edit { prefs ->
            prefs[API_URL] = url
        }
    }

    fun getApiUrl(context: Context): Flow<String> =
        context.dataStore.data.map { prefs ->
            prefs[API_URL] ?: "http://192.168.0.52:5091/"
        }
}