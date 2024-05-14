package com.google.storyapp.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.storyapp.R
import com.google.storyapp.adapter.LoadingStateAdapter
import com.google.storyapp.adapter.StoryAdapter
import com.google.storyapp.databinding.ActivityMainBinding
import com.google.storyapp.preference.LoginPreferences
import com.google.storyapp.viewmodel.MainViewModel
import com.google.storyapp.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mainViewModel: MainViewModel by viewModels { ViewModelFactory(preference) }
    private val preference by lazy { LoginPreferences(this) }
    private val adapter = StoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setFullScreen()
        setView()
        setAdapter()
    }

    private fun setView() {
        with(binding) {
            fab.setOnClickListener {
                startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
            }
            materialToolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.map -> {
                        val intent = Intent(this@MainActivity, MapsActivity::class.java).apply {
                        }
                        startActivity(intent)}
                    R.id.logout -> {
                        showDialog(
                            "Logout",
                            "Yakin anda ingin keluar dari akun ${preference.getUser().name}?"
                        ) {
                            logoutAction()
                        }
                    }
                }
                true
            }
            recyclerview.setOnScrollChangeListener { _, _, newY, _, oldY ->
                if (newY > oldY) binding.fab.shrink() else binding.fab.extend()
            }
            swipeRefreshLayout.setOnRefreshListener {
                adapter.refresh()
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun logoutAction() {
        preference.removeUser()
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }

    private fun setFullScreen() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            binding.appbarLayout.setPadding(0, systemBars.top, 0, 0)
            insets
        }
    }

    private fun setAdapter() {
        binding.recyclerview.adapter =
            adapter.withLoadStateFooter(LoadingStateAdapter { adapter.retry() })
        mainViewModel.getStories().observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    private fun showDialog(
        title: String, message: String, positiveAction: (dialog: DialogInterface) -> Unit
    ) {
        MaterialAlertDialogBuilder(this).apply {
            setTitle(title)
            setMessage(message)
            setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("OK") { dialog, _ ->
                positiveAction.invoke(dialog)
            }
            setCancelable(false)
            create()
            show()
        }
    }
}