package com.google.storyapp.preference

import android.content.Context
import com.google.storyapp.data.AccountLogin

class LoginPreferences(context: Context) {

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setLogin(value: AccountLogin) {
        val editor = preferences.edit()
        editor.putString(NAME, value.name)
        editor.putString(USER_ID, value.userId)
        editor.putString(TOKEN, value.token)
        editor.apply()
    }

    fun getUser(): AccountLogin {
        val name = preferences.getString(NAME, null)
        val userId = preferences.getString(USER_ID, null)
        val token = preferences.getString(TOKEN, null)
        return AccountLogin(userId, name, token)
    }

    fun removeUser() {
        val editor = preferences.edit().clear()
        editor.apply()
    }

    companion object {
        const val PREFS_NAME = "login_pref"
        const val NAME = "name"
        const val USER_ID = "userId"
        const val TOKEN = "token"
    }
}