package com.google.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.storyapp.R
import com.google.storyapp.databinding.ActivityRegisterBinding
import com.google.storyapp.preference.LoginPreferences
import com.google.storyapp.remote.GetResult
import com.google.storyapp.viewmodel.RegisterViewModel
import com.google.storyapp.viewmodel.ViewModelFactory

class RegisterActivity : AppCompatActivity() {

    val loginPreferences by lazy { LoginPreferences(this) }
    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }
    private val registerViewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(
            loginPreferences
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setFullScreen()
        animatedView()
        setView()
    }

    private fun setView() {
        with(binding) {
            buttonRegister.setOnClickListener { checkForm() }
            textLogin.setOnClickListener { finish() }
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

    private fun checkForm() {
        val name = binding.edRegisterName.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()

        if (name.isEmpty()) {
            Toast.makeText(this, "Masukkan nama", Toast.LENGTH_SHORT).show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.length < 8) {
            Toast.makeText(this, "Sandi kurang dari 8", Toast.LENGTH_SHORT).show()
            return
        }
        registeringAccount(name, email, password)
    }

    private fun registeringAccount(name: String, email: String, password: String) {
        registerViewModel.userRegister(name, email, password)
            .observe(this@RegisterActivity) { result ->
                if (result != null) {
                    when (result) {
                        is GetResult.Loading -> {
                            showLoading(true)
                        }

                        is GetResult.Error -> {
                            showLoading(false)
                            showDialog(
                                "Pendaftaran gagal",
                                "Harap coba kembali"
                            ) { it.dismiss() }
                        }

                        is GetResult.Success -> {
                            showLoading(false)
                            registerSuccess()
                        }
                    }
                }
            }
    }

    private fun showDialog(
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

    private fun registerSuccess() {
        showDialog(
            "Berhasil", "Pendaftaran berhasil, silahkan login"
        ) { navigateToLogin() }
        binding.edRegisterName.text?.clear()
        binding.edRegisterEmail.text?.clear()
        binding.edRegisterPassword.text?.clear()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun navigateToLogin() {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}