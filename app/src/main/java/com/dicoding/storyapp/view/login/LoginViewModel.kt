package com.dicoding.storyapp.view.login

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.pref.UserModel
import com.dicoding.storyapp.data.response.ErrorResponse
import com.dicoding.storyapp.data.response.LoginResponse
import com.dicoding.storyapp.data.response.UserResponse
import com.dicoding.storyapp.data.retrofit.ApiConfig
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val repository: StoryRepository) : ViewModel() {
    private val _login = MutableLiveData<Boolean>()
    val login: LiveData<Boolean> = _login

    private val _isLoading = MutableLiveData<Boolean>().apply { value = false }
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<String?>()
    val snackbarText: MutableLiveData<String?> = _snackbarText

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            user.token.let { ApiConfig.updateToken(it) }
            repository.saveSession(user)
        }
    }

    fun login(email: String, password: String) {
        val response = LoginResponse(email, password)
        val client = ApiConfig.getApiService().login(response)
        _isLoading.value = true
        client.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        val token = responseBody.loginResult?.token ?: ""
                        _login.value = true
                        saveSession(UserModel(email, token, true))
                        _snackbarText.value = responseBody.message
                    } else {
                        _login.value = false
                        _snackbarText.value = responseBody?.message ?: "Unknown error"
                    }
                } else {
                    val responseBody = response.errorBody()
                    _login.value = false
                    if (responseBody != null) {
                        val mapper =
                            Gson().fromJson(responseBody.string(), ErrorResponse::class.java)
                        _snackbarText.value = mapper.message
                        Log.e(TAG, "onFailure2: ${mapper.message}")
                    } else {
                        _snackbarText.value = response.message()
                        Log.e(TAG, "onFailure2: ${response.message()}")
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                _isLoading.value = false
                _login.value = false
                _snackbarText.value = t.message ?: "Error !"
                Log.e(TAG, "onFailure: Gagal, ${t.message}")
            }
        })
    }
}