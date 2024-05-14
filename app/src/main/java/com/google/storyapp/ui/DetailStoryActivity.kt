package com.google.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.storyapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {

    private val binding by lazy { ActivityDetailStoryBinding.inflate(layoutInflater) }
    private val username by lazy { intent.getStringExtra(USERNAME) }
    private val desc by lazy { intent.getStringExtra(DESCRIPTION) }
    private val photo by lazy { intent.getStringExtra(PHOTO) }
    private val lat by lazy { intent.getDoubleExtra(LAT, 0.0) }
    private val lon by lazy { intent.getDoubleExtra(LON, 0.0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setFullScreen()
        setView()
    }

    private fun setView() {
        with(binding) {
            Glide.with(this@DetailStoryActivity)
                .load(photo)
                .into(ivDetailPhoto)
            tvDetailName.text = username
            tvDetailDescription.text = desc
            materialToolbar.setNavigationOnClickListener { finish() }
            fabMaps.visibility = if (lat == 0.0 && lon == 0.0) View.GONE else View.VISIBLE
            if (fabMaps.visibility == View.VISIBLE) {
                fabMaps.setOnClickListener {
                    startActivity(Intent(this@DetailStoryActivity, MapsActivity::class.java).apply {
                        putExtra(USERNAME, username)
                        putExtra(LAT, lat)
                        putExtra(LON, lon)
                    })
                }
            }

        }
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

    companion object {
        const val USERNAME = "name"
        const val DESCRIPTION = "desc"
        const val PHOTO = "photo"
        const val LAT = "lat"
        const val LON = "lon"
    }
}