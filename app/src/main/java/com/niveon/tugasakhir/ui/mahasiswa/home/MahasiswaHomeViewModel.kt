package com.niveon.tugasakhir.ui.mahasiswa.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.niveon.tugasakhir.api.RetrofitService
import kotlinx.coroutines.launch
import retrofit2.Response

class MahasiswaHomeViewModel : ViewModel() {
    private val _pertemuanBerlangsungResult = MutableLiveData<Response<JsonObject>?>()
    val pertemuanBerlangsungResult: LiveData<Response<JsonObject>?> get() = _pertemuanBerlangsungResult

    fun getPertemuanBerlangsung(kelaseId: Int) {
        viewModelScope.launch {
            try {
                val requestBody = JsonObject().apply {
                    addProperty("kelase_id", kelaseId)
                }
                val response = RetrofitService.api.getPertemuanBerlangsung(requestBody)
                _pertemuanBerlangsungResult.value = response
            } catch (e: Exception) {
                _pertemuanBerlangsungResult.value = null
            }
        }
    }
}