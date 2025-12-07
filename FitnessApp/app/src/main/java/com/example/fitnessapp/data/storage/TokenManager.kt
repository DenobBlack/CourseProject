package com.example.fitnessapp.data.storage

import android.content.Context

class TokenManager(context: Context):ITokenManager   {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    override fun saveTokens(access: String, refresh: String) {
        prefs.edit()
            .putString("access_token", access)
            .putString("refresh_token", refresh)
            .apply()
    }

    override fun getAccess(): String? = prefs.getString("access_token", null)
    override fun getRefresh(): String? = prefs.getString("refresh_token", null)
    override fun clear() = prefs.edit().clear().apply()
}