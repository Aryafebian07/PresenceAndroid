package com.niveon.tugasakhir.ui.admin.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.niveon.tugasakhir.api.RetrofitService
import com.niveon.tugasakhir.model.Mahasiswas
import kotlinx.coroutines.launch
import retrofit2.Response

class WriteNfcMahasiswaViewModel : ViewModel() {

    private val _mahasiswaList = MutableLiveData<List<Mahasiswas>>()
    val mahasiswaList: LiveData<List<Mahasiswas>> get() = _mahasiswaList
    private val _updateNfcResult: MutableLiveData<Response<JsonObject>?> = MutableLiveData()
    val updateNfcResult: LiveData<Response<JsonObject>?>
        get() = _updateNfcResult

    fun getMahasiswaList(){
        viewModelScope.launch {
            try {
                val response = RetrofitService.api.getMahasiswa()
                if (response.isSuccessful){
                    val mahasiswasResponse = response.body()
                    if (mahasiswasResponse != null && mahasiswasResponse.has("data")) {
                        val mahasiswaArray = mahasiswasResponse.getAsJsonArray("data")
                        val mahasiswas = mutableListOf<Mahasiswas>()
                        mahasiswaArray.forEach { dataElement ->
                            if (dataElement.isJsonObject) {
                                val mahasiswaObject = dataElement.asJsonObject
                                val username = mahasiswaObject.get("username").asString
                                val nama = mahasiswaObject.get("nama").asString
                                mahasiswas.add(Mahasiswas(username, nama))
                            }
                        }
                        _mahasiswaList.value = mahasiswas
                    }else {
                        // Handle error case if necessary
                    }
                } else {
                    // Handle error case if necessary
                }
            }catch (e: Exception) {
                // Handle exception if necessary
            }
        }
    }

    fun updateNfcData(requestBody: JsonObject) {
        viewModelScope.launch {
            try {
                val response = RetrofitService.api.updatenfcdata(requestBody)
                _updateNfcResult.value = response
            } catch (e: Exception) {
                _updateNfcResult.value = null
            }
        }
    }
}
