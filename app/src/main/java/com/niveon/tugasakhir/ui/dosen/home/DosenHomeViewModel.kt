package com.niveon.tugasakhir.ui.dosen.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.niveon.tugasakhir.api.RetrofitService
import kotlinx.coroutines.launch
import retrofit2.Response

class DosenHomeViewModel : ViewModel() {
    private val _pertemuanBerlangsungResult = MutableLiveData<Response<JsonObject>?>()
    val pertemuanBerlangsungResult: LiveData<Response<JsonObject>?> get() = _pertemuanBerlangsungResult

    fun getPertemuanBerlangsung(dosenId: Int) {
        viewModelScope.launch {
            try {
                val requestBody = JsonObject().apply {
                    addProperty("dosen_id", dosenId)
                }
                val response = RetrofitService.api.getPertemuanDosenBerlangsung(requestBody)
                _pertemuanBerlangsungResult.value = response
            } catch (e: Exception) {
                _pertemuanBerlangsungResult.value = null
            }
        }
    }
}