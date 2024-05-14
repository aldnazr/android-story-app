package com.google.storyapp.ui

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.storyapp.BuildConfig
import com.google.storyapp.R
import com.google.storyapp.convertDescription
import com.google.storyapp.convertImage
import com.google.storyapp.createCustomTempFile
import com.google.storyapp.databinding.ActivityAddStoryBinding
import com.google.storyapp.preference.LoginPreferences
import com.google.storyapp.remote.GetResult
import com.google.storyapp.uriToFile
import com.google.storyapp.viewmodel.AddStoryViewModel
import com.google.storyapp.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private val binding by lazy { ActivityAddStoryBinding.inflate(layoutInflater) }
    private val loginPreferences by lazy { LoginPreferences(this) }
    private val addStoryModel: AddStoryViewModel by viewModels {
        ViewModelFactory.getInstance(
            loginPreferences
        )
    }
    private var photoFile: File? = null
    private var photoUri: Uri? = null
    private val CAMERA_PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setFullScreen()
        setView()
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun setView() {
        with(binding) {
            galleryButton.setOnClickListener { pickGalleryImg() }
            cameraButton.setOnClickListener {
                if (isCameraPermissionGranted()) {
                    takePhoto()
                } else {
                    requestCameraPermission()
                }
            }
            materialToolbar.setNavigationOnClickListener { finish() }
            materialToolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.resetImage -> {
                        addStoryModel.setImage(null)
                        photoUri = null
                        showToast("Foto dihapus")
                    }
                }
                true
            }
            buttonAdd.setOnClickListener { addStory() }
            addStoryModel.image.observe(this@AddStoryActivity) {
                if (it != null) {
                    photoUri = it
                    previewImageView.setImageURI(it)
                } else {
                    previewImageView.setImageResource(R.drawable.ic_image)
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

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = createCustomTempFile(this)
        photoUri = FileProvider.getUriForFile(
            this, "${BuildConfig.APPLICATION_ID}.fileprovider",
            photoFile!!
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        launcherCamera.launch(intent)
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            addStoryModel.setImage(photoUri)
        } else {
            photoFile = null
            photoUri = null
        }
    }

    private fun pickGalleryImg() {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherGallery.launch(chooser)
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            photoFile = uriToFile(selectedImg, this)
            addStoryModel.setImage(selectedImg)
        }
    }

    private fun addStory() {
        val desc = binding.edAddDescription.text.toString()
        if (photoFile == null || photoUri == null) {
            showToast("Harap masukkan gambar")
            return
        }
        if (desc.isEmpty()) {
            showToast("Harap masukkan deskripsi")
            return
        }
        lifecycleScope.launch {
            postStory(desc)
        }
    }

    private fun postStory(description: String) {
        val image = convertImage(photoFile)
        val desc = convertDescription(description)
        if (image != null) {
            addStoryModel.insertStory(image, desc).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is GetResult.Loading -> {
                            showLoading(true)
                        }

                        is GetResult.Error -> {
                            showLoading(false)
                            showAlert(
                                "Gagal mengirim", "Harap coba kembali"
                            ) { }
                        }

                        is GetResult.Success -> {
                            showLoading(false)
                            showToast("Berhasil diposting")
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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

    private fun showToast(message: String) {
        Toast.makeText(
            this, message, Toast.LENGTH_SHORT
        ).show()
    }
}