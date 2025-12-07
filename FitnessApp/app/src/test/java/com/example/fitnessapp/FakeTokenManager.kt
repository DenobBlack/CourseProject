package com.example.fitnessapp

import com.example.fitnessapp.data.storage.ITokenManager

class FakeTokenManager : ITokenManager {

    private var accessToken: String? = null
    private var refreshToken: String? = null

    override fun saveTokens(access: String, refresh: String) {
        accessToken = access
        refreshToken = refresh
    }

    override fun getAccess(): String? = accessToken

    override fun getRefresh(): String? = refreshToken

    override fun clear() {
        accessToken = null
        refreshToken = null
    }
}
