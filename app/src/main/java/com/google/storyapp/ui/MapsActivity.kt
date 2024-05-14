package com.google.storyapp.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.storyapp.R
import com.google.storyapp.databinding.ActivityMapsBinding
import com.google.storyapp.preference.LoginPreferences
import com.google.storyapp.remote.GetResult
import com.google.storyapp.remote.response.Story
import com.google.storyapp.viewmodel.MapsViewModel
import com.google.storyapp.viewmodel.ViewModelFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var gMap: GoogleMap
    private val binding by lazy { ActivityMapsBinding.inflate(layoutInflater) }
    private val username by lazy { intent.getStringExtra(DetailStoryActivity.USERNAME) }
    private val lat by lazy { intent.getDoubleExtra(DetailStoryActivity.LAT, 0.0) }
    private val lon by lazy { intent.getDoubleExtra(DetailStoryActivity.LON, 0.0) }
    private val boundsBuilder = LatLngBounds.Builder()
    private val preference by lazy { LoginPreferences(this) }
    private val mapsViewModel by viewModels<MapsViewModel> { ViewModelFactory(preference) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setFullScreen()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap.uiSettings.isZoomControlsEnabled = true
        gMap.uiSettings.isIndoorLevelPickerEnabled = true
        gMap.uiSettings.isCompassEnabled = true
        val currentLocation: LatLng
        if (username != null) {
            currentLocation = LatLng(lat, lon)
            gMap.addMarker(MarkerOptions().apply {
                position(currentLocation)
                title(username)
                snippet("$lat, $lon")
            })
        } else {
            currentLocation = LatLng(-7.1485798868927795, 110.12189203804503)
            getAllStoryWithLocation(googleMap)
        }
        gMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
    }

    private fun getAllStoryWithLocation(googleMap: GoogleMap) {
        mapsViewModel.getStoriesLocation().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is GetResult.Loading -> {
                        showLoading(true)
                    }

                    is GetResult.Error -> {
                        showLoading(false)
                    }

                    is GetResult.Success -> {
                        showLoading(false)
                        addManyMarker(result.data.listStory, googleMap)
                    }
                }
            }
        }
    }

    private fun addManyMarker(listStory: List<Story>, googleMap: GoogleMap) {
        listStory.forEach { story ->
            val latLng = LatLng(story.lat, story.lon)
            googleMap.addMarker(
                MarkerOptions().position(latLng).title(story.name)
                    .snippet("${story.lat}, ${story.lon}")
            )
            boundsBuilder.include(latLng)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setFullScreen() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
    }
}