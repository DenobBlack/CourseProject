package com.example.fitnessapp.data.network

import android.content.Context
import com.example.fitnessapp.data.repository.ApiPreferences
import com.example.fitnessapp.data.storage.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// http://10.0.2.2
// http://192.168.0.52
object RetrofitClient {
    private const val BASE_URL = "http://192.168.0.52:5091/"

    fun create(context: Context): ApiService {
        val tokenManager = TokenManager(context)



        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager, lazyApi(BASE_URL)))
            .addInterceptor(NetworkConnectionInterceptor(context))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    fun createPublic(context: Context): ApiService {
        val apiUrl = runBlocking {
            ApiPreferences.getApiUrl(context).first()
        }

        return Retrofit.Builder()
            .baseUrl(apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private fun lazyApi(baseUrl: String): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ApiService::class.java)
    }
}