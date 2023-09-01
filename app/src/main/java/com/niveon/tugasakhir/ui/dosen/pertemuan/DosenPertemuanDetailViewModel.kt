package com.niveon.tugasakhir.ui.dosen.pertemuan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.niveon.tugasakhir.api.RetrofitService
import kotlinx.coroutines.launch
import retrofit2.Response

class DosenPertemuanDetailViewModel : ViewModel() {
    private val _pertemuanDetailResult = MutableLiveData<Response<JsonObject>?>()
    val pertemuanDetailResult: LiveData<Response<JsonObject>?> get() = _pertemuanDetailResult

    fun getPertemuanDetail(Id: Int,idMahasiswa: Int) {
        viewModelScope.launch {
            try {
                val requestBody = JsonObject().apply {
                    addProperty("id", Id)
                }
                val response = RetrofitService.api.getPertemuanDosenDetail(requestBody)
                _pertemuanDetailResult.value = response
            } catch (e: Exception) {
                _pertemuanDetailResult.value = null
            }
        }
    }
}