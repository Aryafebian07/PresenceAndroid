package com.niveon.tugasakhir.ui.mahasiswa.pertemuan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.niveon.tugasakhir.api.RetrofitService
import kotlinx.coroutines.launch
import retrofit2.Response

class MahasiswaPertemuanRiwayatViewModel : ViewModel() {
    private val _pertemuanRiwayatResult = MutableLiveData<Response<JsonObject>?>()
    val pertemuanRiwayatResult: LiveData<Response<JsonObject>?> get() = _pertemuanRiwayatResult

    fun getPertemuanRiwayat(kelaseId: Int) {
        viewModelScope.launch {
            try {
                val requestBody = JsonObject().apply {
                    addProperty("kelase_id", kelaseId)
                }
                val response = RetrofitService.api.getPertemuanRiwayat(requestBody)
                _pertemuanRiwayatResult.value = response
            } catch (e: Exception) {
                _pertemuanRiwayatResult.value = null
            }
        }
    }
}