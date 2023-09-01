package com.niveon.tugasakhir.ui.dosen.jadwal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.niveon.tugasakhir.api.RetrofitService
import kotlinx.coroutines.launch
import retrofit2.Response

class DosenJadwalViewModel : ViewModel() {
    private val _jadwalResult = MutableLiveData<Response<JsonObject>?>()
    val jadwalResult: LiveData<Response<JsonObject>?> get() = _jadwalResult

    fun getJadwalList(dosenId: Int) {
        viewModelScope.launch {
            try {
                val requestBody = JsonObject().apply {
                    addProperty("dosen_id", dosenId)
                }
                val response = RetrofitService.api.getJadwaldosens(requestBody)
                _jadwalResult.value = response
            } catch (e: Exception) {
                _jadwalResult.value = null
            }
        }
    }
}