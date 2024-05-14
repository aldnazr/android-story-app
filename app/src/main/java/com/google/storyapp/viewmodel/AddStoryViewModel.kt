package com.google.storyapp.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.storyapp.preference.LoginPreferences
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(
    loginPreferences: LoginPreferences
) : ViewModel() {

    private val repository = Repository(loginPreferences)
    private val _image = MutableLiveData<Uri?>()
    val image: LiveData<Uri?> = _image

    fun insertStory(imageFile: MultipartBody.Part, desc: RequestBody) =
        repository.addStory(imageFile, desc)

    fun setImage(imageUri: Uri?) {
        _image.value = imageUri
    }
}