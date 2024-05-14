package com.google.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.storyapp.data.StoryPagingSource
import com.google.storyapp.preference.LoginPreferences
import com.google.storyapp.remote.GetResult
import com.google.storyapp.remote.response.AddStoryResponse
import com.google.storyapp.remote.response.GetStoryResponse
import com.google.storyapp.remote.response.LoginResponse
import com.google.storyapp.remote.response.RegisterResponse
import com.google.storyapp.remote.response.Story
import com.google.storyapp.remote.retrofit.ApiConfig
import com.google.storyapp.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class Repository(
    private val loginPreferences: LoginPreferences
) {

    private val apiService: ApiService = ApiConfig.getApiService()

    fun register(
        name: String, email: String, password: String
    ): LiveData<GetResult<RegisterResponse>> = liveData {
        emit(GetResult.Loading)
        try {
            val response = apiService.register(
                name, email, password
            )
            if (response.error) {
                emit(GetResult.Error(response.message))
            } else {
                emit(GetResult.Success(response))
            }
        } catch (e: Exception) {
            emit(GetResult.Error(e.message.toString()))
        }
    }

    fun login(
        email: String, password: String
    ): LiveData<GetResult<LoginResponse>> = liveData {
        emit(GetResult.Loading)
        try {
            val response = apiService.login(
                email, password
            )
            if (response.error) {
                emit(GetResult.Error(response.message))
            } else {
                emit(GetResult.Success(response))
            }
        } catch (e: Exception) {
            emit(GetResult.Error(e.message.toString()))
        }
    }

    fun addStory(
        imageFile: MultipartBody.Part, desc: RequestBody
    ): LiveData<GetResult<AddStoryResponse>> = liveData {
        emit(GetResult.Loading)
        try {
            val response = apiService.createStory(
                "Bearer ${loginPreferences.getUser().token}", imageFile, desc
            )
            if (response.error) {
                emit(GetResult.Error(response.message))
            } else {
                emit(GetResult.Success(response))
            }
        } catch (e: Exception) {
            emit(GetResult.Error(e.message.toString()))
        }
    }

    fun getStory(): LiveData<PagingData<Story>> {
        return Pager(
            PagingConfig(5), null
        ) { StoryPagingSource(loginPreferences, apiService) }.liveData
    }

    fun listStoryLoc(): LiveData<GetResult<GetStoryResponse>> = liveData {
        emit(GetResult.Loading)
        try {
            val response = apiService.getStoryLoc(
                token = "Bearer ${loginPreferences.getUser().token}",
                page = 1,
                size = 100,
                location = 1
            )
            if (response.error) {
                emit(GetResult.Error(response.message))
            } else {
                emit(GetResult.Success(response))
            }
        } catch (e: Exception) {
            emit(GetResult.Error(e.message.toString()))
        }
    }
}