package com.example.fitnessapp.data.storage

interface ITokenManager {
    fun saveTokens(access: String, refresh: String)
    fun getAccess(): String?
    fun getRefresh(): String?
    fun clear()
}