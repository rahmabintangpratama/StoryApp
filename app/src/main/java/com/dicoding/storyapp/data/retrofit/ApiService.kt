package com.dicoding.storyapp.data.retrofit

import com.dicoding.storyapp.data.response.DetailStoryResponse
import com.dicoding.storyapp.data.response.LoginResponse
import com.dicoding.storyapp.data.response.PostResponse
import com.dicoding.storyapp.data.response.RegisterResponse
import com.dicoding.storyapp.data.response.SignupResponse
import com.dicoding.storyapp.data.response.StoryResponse
import com.dicoding.storyapp.data.response.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @Headers("No-Authentication: true")
    @POST("login")
    fun login(
        @Body loginResponse: LoginResponse
    ): Call<UserResponse>

    @Headers("No-Authentication: true")
    @POST("register")
    fun register(
        @Body registerResponse: RegisterResponse
    ): Call<SignupResponse>

    @GET("stories")
    fun getStories(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int = 0
    ): Call<StoryResponse>

    @GET("stories/{userId}")
    fun getDetailStory(
        @Path("userId") userId: String
    ): Call<DetailStoryResponse>

    @Multipart
    @POST("stories")
    fun postStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<PostResponse>

    @GET("stories")
    suspend fun getMaps(
        @Query("location") location: Int = 1
    ): StoryResponse
}