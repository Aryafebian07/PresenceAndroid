package com.niveon.tugasakhir.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niveon.tugasakhir.model.AdminResponse
import com.niveon.tugasakhir.model.DosenResponse
import com.niveon.tugasakhir.model.MahasiswaResponse

class SharedViewModel : ViewModel() {
    private val _adminResponse = MutableLiveData<AdminResponse>()
    val adminResponse: LiveData<AdminResponse> get() = _adminResponse

    private val _dosenResponse = MutableLiveData<DosenResponse>()
    val dosenResponse: LiveData<DosenResponse> get() = _dosenResponse

    private val _mahasiswaResponse = MutableLiveData<MahasiswaResponse>()
    val mahasiswaResponse: LiveData<MahasiswaResponse> get() = _mahasiswaResponse

    fun setAdminResponse(response: AdminResponse) {
        _adminResponse.value = response
    }

    fun setDosenResponse(response: DosenResponse) {
        _dosenResponse.value = response
    }

    fun setMahasiswaResponse(response: MahasiswaResponse) {
        _mahasiswaResponse.value = response
    }
}
