package com.niveon.tugasakhir.ui.mahasiswa.pertemuan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.niveon.tugasakhir.api.RetrofitService
import kotlinx.coroutines.launch
import retrofit2.Response

class MahasiswaPertemuanDetailViewModel : ViewModel() {
    private val _pertemuanDetailResult = MutableLiveData<Response<JsonObject>?>()
    val pertemuanDetailResult: LiveData<Response<JsonObject>?> get() = _pertemuanDetailResult

    private val _absenResult = MutableLiveData<Response<JsonObject>?>()
    val absenResult: LiveData<Response<JsonObject>?> get() = _absenResult


    fun getPertemuanDetail(Id: Int,idMahasiswa: Int) {
        viewModelScope.launch {
            try {
                val requestBody = JsonObject().apply {
                    addProperty("id", Id)
                    addProperty("mahasiswa_id", idMahasiswa)
                }
                val response = RetrofitService.api.getPertemuanDetail(requestBody)
                _pertemuanDetailResult.value = response
            } catch (e: Exception) {
                _pertemuanDetailResult.value = null
            }
        }
    }

    fun absen(requestBody: JsonObject) {
        viewModelScope.launch {
            try {
                val response = RetrofitService.api.absenmahasiswa(requestBody)
                _absenResult.value = response
            } catch (e: Exception) {
                _absenResult.value = null
            }
        }
    }
}