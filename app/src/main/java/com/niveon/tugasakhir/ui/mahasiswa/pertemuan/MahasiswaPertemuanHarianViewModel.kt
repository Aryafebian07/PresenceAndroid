package com.niveon.tugasakhir.ui.mahasiswa.pertemuan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.niveon.tugasakhir.api.RetrofitService
import kotlinx.coroutines.launch
import retrofit2.Response

class MahasiswaPertemuanHarianViewModel : ViewModel() {
    private val _pertemuanHarianResult = MutableLiveData<Response<JsonObject>?>()
    val pertemuanHarianResult: LiveData<Response<JsonObject>?> get() = _pertemuanHarianResult

    fun getPertemuanHarian(kelaseId: Int) {
        viewModelScope.launch {
            try {
                val requestBody = JsonObject().apply {
                    addProperty("kelase_id", kelaseId)
                }
                val response = RetrofitService.api.getPertemuanHarian(requestBody)
                _pertemuanHarianResult.value = response
            } catch (e: Exception) {
                _pertemuanHarianResult.value = null
            }
        }
    }
}