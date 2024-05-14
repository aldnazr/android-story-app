package com.google.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.google.storyapp.preference.LoginPreferences

class LoginViewModel(
    loginPreferences: LoginPreferences
) : ViewModel() {

    private val repository = Repository(loginPreferences)

    fun userLogin(email: String, password: String) = repository.login(email, password)
}