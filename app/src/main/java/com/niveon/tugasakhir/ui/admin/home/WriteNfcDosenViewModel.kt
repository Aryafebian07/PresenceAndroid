package com.niveon.tugasakhir.ui.admin.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.niveon.tugasakhir.api.RetrofitService
import com.niveon.tugasakhir.model.Dosens
import kotlinx.coroutines.launch
import retrofit2.Response

class WriteNfcDosenViewModel : ViewModel() {

    private val _dosenList = MutableLiveData<List<Dosens>>()
    val dosenList: LiveData<List<Dosens>> get() = _dosenList
    private val _updateNfcResult: MutableLiveData<Response<JsonObject>?> = MutableLiveData()
    val updateNfcResult: LiveData<Response<JsonObject>?>
        get() = _updateNfcResult

    fun getDosenList(){
        viewModelScope.launch {
            try {
                val response = RetrofitService.api.getDosen()
                if (response.isSuccessful){
                    val dosensResponse = response.body()
                    if (dosensResponse != null && dosensResponse.has("data")) {
                        val dosenArray = dosensResponse.getAsJsonArray("data")
                        val dosens = mutableListOf<Dosens>()
                        dosenArray.forEach { dataElement ->
                            if (dataElement.isJsonObject) {
                                val dosenObject = dataElement.asJsonObject
                                val username = dosenObject.get("username").asString
                                val nama = dosenObject.get("nama").asString
                                dosens.add(Dosens(username, nama))
                            }
                        }
                        _dosenList.value = dosens
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
