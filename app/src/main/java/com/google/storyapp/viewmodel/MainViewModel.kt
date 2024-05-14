package com.google.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.storyapp.preference.LoginPreferences
import com.google.storyapp.remote.response.Story

class MainViewModel(
    private val repository: Repository
) : ViewModel() {

    constructor(loginPreferences: LoginPreferences) : this(Repository(loginPreferences))

    fun getStories(): LiveData<PagingData<Story>> = repository.getStory()
}