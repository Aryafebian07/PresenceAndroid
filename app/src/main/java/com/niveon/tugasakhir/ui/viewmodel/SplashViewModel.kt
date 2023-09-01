package com.niveon.tugasakhir.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SplashViewModel : ViewModel() {
    private val _splashFinished = MutableLiveData<Boolean>()
    val splashFinished: LiveData<Boolean> = _splashFinished

    fun setSplashFinished(finished: Boolean) {
        _splashFinished.value = finished
    }
}