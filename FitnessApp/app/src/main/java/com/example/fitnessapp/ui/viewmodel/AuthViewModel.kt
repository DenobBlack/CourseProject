package com.example.fitnessapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.model.LoginRequest
import com.example.fitnessapp.data.model.UiState
import com.example.fitnessapp.data.model.UserProfileDto
import com.example.fitnessapp.data.model.UserRegisterRequest
import com.example.fitnessapp.data.network.NoInternetException
import com.example.fitnessapp.data.network.RetrofitClient
import com.example.fitnessapp.data.storage.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AuthViewModel(private val context: Context) : ViewModel() {

    private val tokenManager = TokenManager(context)
    private val api = RetrofitClient.create(context)

    val uiState = MutableStateFlow<UiState>(UiState.Loading)
    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = api.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        tokenManager.saveTokens(body.accessToken, body.refreshToken)
                        _loginSuccess.postValue(true)
                    } else {
                        _error.postValue("Пустой ответ от сервера")
                    }
                } else {
                    _error.postValue(parseErrorMessage(response, "Ошибка авторизации"))
                }
            }  catch (e: NoInternetException) {
                uiState.value = UiState.Error("Нет соединения с интернетом")
            } catch (e: Exception) {
                uiState.value = UiState.Error("Ошибка сервера")
            }
        }
    }

    fun register(
        email: String,
        username: String,
        password: String,
        gender: String,
        birthDate: String,
        height: Int,
        weight: Int,
        name: String,
        lastName: String,
        patronymic: String?,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val now = LocalDateTime.now().format(formatter)

                val request = UserRegisterRequest(
                    userId = 0,
                    username = username,
                    email = email,
                    passwordHash = password,
                    gender = gender,
                    birthDate = birthDate,
                    heightCm = height,
                    weightKg = weight,
                    createdAt = now,
                    name = name,
                    lastName = lastName,
                    patronymic = patronymic
                )

                val response = RetrofitClient.createPublic(context).register(request)

                if (response.isSuccessful) {
                    onResult(true, "Регистрация прошла успешно 🎉")
                } else {
                    onResult(false, parseErrorMessage(response, "Ошибка регистрации"))
                }

            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false, "Проблема с подключением к серверу")
            }catch (e: NoInternetException) {
                uiState.value = UiState.Error("Нет соединения с интернетом")
            }
        }
    }

    var userId: Int? = null
        get() = decodeJwtUserId(tokenManager.getAccess())

    val username: String?
        get() = decodeJwtUsername(tokenManager.getAccess())
    val roleName: String?
        get() = decodeJwtRole(tokenManager.getAccess())

    private val _profile = MutableStateFlow<UserProfileDto?>(null)
    val profile: StateFlow<UserProfileDto?> = _profile

    fun loadProfile(userId: Int) {
        viewModelScope.launch {
            try {
                val res = api.getUserProfile(userId)
                if (res.isSuccessful) {
                    _profile.value = res.body()
                } else {
                    _profile.value = null
                    println("PROFILE ERROR: ${res.code()}")
                }
            } catch (e: Exception) {
                _profile.value = null
                e.printStackTrace()
            }
        }
    }

    private fun decodeJwtUserId(token: String?): Int? {
        if (token == null) return null
        return try {
            val payload = String(
                android.util.Base64.decode(
                    token.split(".")[1],
                    android.util.Base64.URL_SAFE
                )
            )
            JSONObject(payload)
                .getString("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier")
                .toInt()
        } catch (e: Exception) {
            null
        }
    }

    private fun decodeJwtUsername(token: String?): String? {
        if (token == null) return null
        return try {
            val payload = String(
                android.util.Base64.decode(
                    token.split(".")[1],
                    android.util.Base64.URL_SAFE
                )
            )
            JSONObject(payload)
                .getString("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name")
        } catch (e: Exception) {
            null
        }
    }
    private fun decodeJwtRole(token: String?): String? {
        if (token == null) return null
        return try {
            val payload = String(
                android.util.Base64.decode(
                    token.split(".")[1],
                    android.util.Base64.URL_SAFE
                )
            )
            JSONObject(payload)
                .getString("http://schemas.microsoft.com/ws/2008/06/identity/claims/role")
        } catch (e: Exception) {
            null
        }
    }
    fun updateProfile(dto: UserProfileDto) {
        viewModelScope.launch {
            api.updateUserProfile(dto.userId, dto)
            loadProfile(dto.userId)
        }
    }
    fun logout() {
        tokenManager.clear()
        _loginSuccess.postValue(false)
    }
}

private fun parseErrorMessage(response: Response<*>, defaultMessage: String): String {
    return try {
        val errorBody = response.errorBody()?.string()
        if (!errorBody.isNullOrEmpty()) {
            val json = JSONObject(errorBody)

            if (json.has("errors")) {
                val errorsObj = json.getJSONObject("errors")
                val messages = mutableListOf<String>()

                errorsObj.keys().forEach { key ->
                    val arr = errorsObj.getJSONArray(key)
                    for (i in 0 until arr.length()) {
                        messages.add(arr.getString(i))
                    }
                }

                // Возвращаем все ошибки в одну строку, разделённые переносами
                return messages.joinToString("\n")
            }

            // Если в ответе есть просто "message"
            if (json.has("message")) {
                return json.getString("message")
            }

            // fallback, если структура нестандартная
            json.optString("title", defaultMessage)
        } else {
            when (response.code()) {
                400 -> "Некорректные данные. Проверьте заполненные поля "
                401 -> "Неверный логин или пароль"
                403 -> "Доступ запрещён"
                404 -> "Ресурс не найден"
                409 -> "Такой пользователь уже существует"
                500 -> "Ошибка на сервере "
                else -> "$defaultMessage (${response.code()})"
            }
        }
    } catch (e: Exception) {
        "$defaultMessage (ошибка обработки ответа)"
    }

}



