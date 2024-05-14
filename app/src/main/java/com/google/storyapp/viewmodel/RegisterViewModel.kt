package com.google.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.google.storyapp.preference.LoginPreferences

class RegisterViewModel(
    loginPreferences: LoginPreferences
) : ViewModel() {

    private val repository = Repository(loginPreferences)

    fun userRegister(name: String, email: String, password: String) =
        repository.register(name, email, password)

}