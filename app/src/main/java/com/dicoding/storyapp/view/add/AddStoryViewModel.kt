package com.dicoding.storyapp.view.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.response.PostResponse
import com.dicoding.storyapp.data.retrofit.ApiConfig
import com.dicoding.storyapp.utils.reduceFileImage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryViewModel : ViewModel() {

    private val _isPosted = MutableLiveData<Boolean>()
    val isPosted: LiveData<Boolean> = _isPosted

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun postStory(file: File, description: String) {
        _isLoading.value = true
        val compressedFile = file.reduceFileImage()
        val desc = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart =
            MultipartBody.Part.createFormData("photo", compressedFile.name, requestImageFile)

        val client = ApiConfig.getApiService().postStory(imageMultipart, desc)
        client.enqueue(object : Callback<PostResponse> {
            override fun onResponse(call: Call<PostResponse>, response: Response<PostResponse>) {
                _isLoading.value = false
                _isPosted.value = response.isSuccessful && response.body()?.error == false
            }

            override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                _isLoading.value = false
                _isPosted.value = false
            }
        })
    }
}