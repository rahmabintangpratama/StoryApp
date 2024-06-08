package com.dicoding.storyapp.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.Results
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.response.Story

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getMaps(): LiveData<Results<List<Story>>> {
        return storyRepository.getMaps()
    }
}