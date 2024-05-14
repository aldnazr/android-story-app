package com.google.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.google.storyapp.preference.LoginPreferences

class MapsViewModel(loginPreferences: LoginPreferences) : ViewModel() {
    private val repository = Repository(loginPreferences)

    fun getStoriesLocation() = repository.listStoryLoc()
}