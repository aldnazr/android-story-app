package com.google.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.storyapp.viewmodel.LoginViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.storyapp.R
import com.google.storyapp.data.AccountLogin
import com.google.storyapp.databinding.ActivityLoginBinding
import com.google.storyapp.preference.LoginPreferences
import com.google.storyapp.remote.GetResult
import com.google.storyapp.remote.response.LoginResponse
import com.google.storyapp.viewmodel.ViewModelFactory

class LoginActivity : AppCompatActivity() {

    private val loginPreferences by lazy { LoginPreferences(this) }
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val loginViewModel by viewModels<LoginViewModel> { ViewModelFactory.getInstance(loginPreferences) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setFullScreen()
        animatedView()
        binding.createAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.loginButton.setOnClickListener { processLogin() }
    }

    private fun processLogin() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.isNotEmpty()) {
                checkLogin(email, password)
            } else {
                showAlert(
                    "Login gagal", "Harap masukkan email dan sandi dengan benar"
                ) { }
            }
        }
    }

    private fun animatedView() {
        val nestedScrollAnim =
            ObjectAnimator.ofFloat(binding.nestedScrollView, View.ALPHA, 1F).setDuration(1000L)

        AnimatorSet().apply {
            play(nestedScrollAnim)
            start()
        }
    }

    private fun setFullScreen() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun checkLogin(email: String, password: String) {
        loginViewModel.userLogin(email, password).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is GetResult.Loading -> {
                        showLoading(true)
                    }

                    is GetResult.Error -> {
                        showLoading(false)
                        showAlert(
                            "Login gagal", "Email atau sandi salah"
                        ) { }
                    }

                    is GetResult.Success -> {
                        showLoading(false)
                        saveLoginData(result.data)
                        navigateToMain()
                    }
                }
            }
        }
    }

    private fun showAlert(
        title: String, message: String, positiveAction: (dialog: DialogInterface) -> Unit
    ) {
        MaterialAlertDialogBuilder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { dialog, _ ->
                positiveAction.invoke(dialog)
            }
            setCancelable(false)
            create()
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun saveLoginData(loginResponse: LoginResponse) {
        val loginPreference = LoginPreferences(this)
        val loginResult = loginResponse.loginResult
        val accountLogin = AccountLogin(
            name = loginResult?.name, userId = loginResult?.userId, token = loginResult?.token
        )
        loginPreference.setLogin(accountLogin)
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}