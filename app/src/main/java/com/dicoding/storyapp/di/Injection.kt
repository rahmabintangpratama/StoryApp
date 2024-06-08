package com.dicoding.storyapp.di

import android.content.Context
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.pref.UserPreference
import com.dicoding.storyapp.data.pref.dataStore
import com.dicoding.storyapp.data.retrofit.ApiConfig
import com.dicoding.storyapp.data.room.StoryDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getInstance(context)
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        ApiConfig.updateToken(user.token)
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(database, pref, apiService)
    }
}