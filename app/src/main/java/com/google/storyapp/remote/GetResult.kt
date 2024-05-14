package com.google.storyapp.remote

sealed class GetResult<out R> private constructor() {
    data class Success<out T>(val data: T) : GetResult<T>()
    data class Error(val data: String) : GetResult<Nothing>()
    data object Loading : GetResult<Nothing>()
}