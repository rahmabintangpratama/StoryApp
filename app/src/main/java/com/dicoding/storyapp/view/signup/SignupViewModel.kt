package com.dicoding.storyapp.view.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.response.ErrorResponse
import com.dicoding.storyapp.data.response.RegisterResponse
import com.dicoding.storyapp.data.response.SignupResponse
import com.dicoding.storyapp.data.retrofit.ApiConfig
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupViewModel : ViewModel() {

    private val _signup = MutableLiveData<Boolean>()
    val signup: LiveData<Boolean> = _signup

    private val _isLoading = MutableLiveData<Boolean>().apply { value = false }
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<String?>()
    val snackbarText: LiveData<String?> = _snackbarText

    fun signup(name: String, email: String, password: String) {
        val response = RegisterResponse(name, email, password)
        val client = ApiConfig.getApiService().register(response)
        _isLoading.value = true
        client.enqueue(object : Callback<SignupResponse> {
            override fun onResponse(
                call: Call<SignupResponse>,
                response: Response<SignupResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _signup.value = true
                        _snackbarText.value = responseBody.message
                    } else {
                        _signup.value = false
                        _snackbarText.value = responseBody?.message ?: "Unknown error"
                    }
                } else {
                    val responseBody = response.errorBody()
                    _signup.value = false
                    if (responseBody != null) {
                        val mapper =
                            Gson().fromJson(responseBody.string(), ErrorResponse::class.java)
                        _snackbarText.value = mapper.message
                        Log.e("SignupViewModel", "onFailure2: ${mapper.message}")
                    } else {
                        _snackbarText.value = response.message()
                        Log.e("SignupViewModel", "onFailure2: ${response.message()}")
                    }
                }
            }

            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                _isLoading.value = false
                _signup.value = false
                _snackbarText.value = t.message ?: "Error!"
                Log.e("SignupViewModel", "onFailure: Gagal, ${t.message}")
            }
        })
    }
}