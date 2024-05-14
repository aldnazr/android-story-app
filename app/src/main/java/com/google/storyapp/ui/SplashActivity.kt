package com.google.storyapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.storyapp.R
import com.google.storyapp.preference.LoginPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val launchTime = 2000L
    private val accountLogin by lazy { LoginPreferences(this).getUser() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        setFullScreen()
        launchApp()
    }

    private fun setFullScreen() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun launchApp() {
        lifecycleScope.launch {
            delay(launchTime)
            withContext(Dispatchers.Main) {
                val intent = if (accountLogin.token.isNullOrEmpty()) {
                    Intent(this@SplashActivity, LoginActivity::class.java)
                } else {
                    Intent(this@SplashActivity, MainActivity::class.java)
                }
                startActivity(intent)
                finish()
            }
        }
    }
}