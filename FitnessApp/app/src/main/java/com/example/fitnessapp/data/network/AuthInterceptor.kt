package com.example.fitnessapp.data.network

import com.example.fitnessapp.data.model.RefreshRequest
import com.example.fitnessapp.data.storage.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenManager: TokenManager,
    private val api: ApiService
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val access = tokenManager.getAccess()

        if (access != null) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $access")
                .build()
        }

        val response = chain.proceed(request)

        if (response.code == 401) {
            response.close()
            val refresh = tokenManager.getRefresh() ?: return response
            val refreshResponse = runBlocking { api.refresh(RefreshRequest(refresh)) }

            if (refreshResponse.isSuccessful) {
                val body = refreshResponse.body()!!
                tokenManager.saveTokens(body.accessToken, body.refreshToken)

                val newReq = request.newBuilder()
                    .removeHeader("Authorization")
                    .addHeader("Authorization", "Bearer ${body.accessToken}")
                    .build()

                return chain.proceed(newReq)
            }
        }

        return response
    }
}