package com.niveon.tugasakhir.ui.mahasiswa.jadwal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.niveon.tugasakhir.api.RetrofitService
import kotlinx.coroutines.launch
import retrofit2.Response

class MahasiswaJadwalViewModel : ViewModel() {
    // properti dan fungsi lainnya

    private val _jadwalResult = MutableLiveData<Response<JsonObject>?>()
    val jadwalResult: LiveData<Response<JsonObject>?> get() = _jadwalResult

    fun getJadwalList(kelaseId: Int) {
        viewModelScope.launch {
            try {
                val requestBody = JsonObject().apply {
                    addProperty("kelase_id", kelaseId)
                }
                val response = RetrofitService.api.getJadwalmahasiswas(requestBody)
                _jadwalResult.value = response
            } catch (e: Exception) {
                _jadwalResult.value = null
            }
        }
    }
}

