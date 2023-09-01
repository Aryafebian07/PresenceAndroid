package com.niveon.tugasakhir.ui.mahasiswa.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.niveon.tugasakhir.api.RetrofitService
import kotlinx.coroutines.launch
import retrofit2.Response

class MahasiswaProfileViewModel : ViewModel() {
    private val _dataMahasiswaResult = MutableLiveData<Response<JsonObject>?>()
    val dataMahasiswaResult: LiveData<Response<JsonObject>?> get() = _dataMahasiswaResult

    fun getDataMahasiswa(idMahasiswa: Int) {
        viewModelScope.launch {
            try {
                val requestBody = JsonObject().apply {
                    addProperty("id", idMahasiswa)
                }
                val response = RetrofitService.api.getProfileMahasiswa(requestBody)
                _dataMahasiswaResult.value = response
            } catch (e: Exception) {
                _dataMahasiswaResult.value = null
            }
        }
    }

}