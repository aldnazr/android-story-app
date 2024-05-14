package com.google.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.storyapp.preference.LoginPreferences

class ViewModelFactory(
    private val loginPreferences: LoginPreferences
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> AddStoryViewModel(
                loginPreferences
            ) as T

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(
                loginPreferences
            ) as T

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> RegisterViewModel(
                loginPreferences
            ) as T

            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(
                loginPreferences
            ) as T

            modelClass.isAssignableFrom(MapsViewModel::class.java) -> MapsViewModel(
                loginPreferences
            ) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(
            loginPreferences: LoginPreferences
        ): ViewModelFactory = INSTANCE ?: synchronized(this) {
            INSTANCE ?: ViewModelFactory(loginPreferences)
        }
    }
}