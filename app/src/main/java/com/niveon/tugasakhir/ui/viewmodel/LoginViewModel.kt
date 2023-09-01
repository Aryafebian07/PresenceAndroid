package com.niveon.tugasakhir.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.google.gson.JsonObject
import com.niveon.tugasakhir.api.RetrofitService
import com.niveon.tugasakhir.model.AdminResponse
import com.niveon.tugasakhir.model.DosenResponse
import com.niveon.tugasakhir.model.LoginRequest
import com.niveon.tugasakhir.model.MahasiswaResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class LoginViewModel : ViewModel() {
    private val TAG = "LoginViewModel"

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult

    private val _adminResult = MutableLiveData<AdminResponse?>()
    val adminResult: LiveData<AdminResponse?> = _adminResult

    private val _mahasiswaResult = MutableLiveData<MahasiswaResponse?>()
    val mahasiswaResult: LiveData<MahasiswaResponse?> = _mahasiswaResult

    private val _dosenResult = MutableLiveData<DosenResponse?>()
    val dosenResult: LiveData<DosenResponse?> = _dosenResult

    private val _loginResponseMessage = MutableLiveData<String>()
    val loginResponseMessage: LiveData<String> = _loginResponseMessage

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val request = LoginRequest(username, password)
                val response = RetrofitService.api.login(request)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.success == true) {
                        val token = loginResponse.data.token
                        try {
                            val userResponse = RetrofitService.api.getUser("Bearer $token")
                            if (userResponse.isSuccessful) {
                                val userBody = userResponse.body()
                                val role = userBody?.get("role")?.asString?.toLowerCase()

                                when (role) {
                                    "admin" -> {
                                        val adminResponse = userBody?.let {
                                            AdminResponse(
                                                id = it.get("id").asInt,
                                                nama = it.get("nama")?.asString ?: "",
                                                username = it.get("username")?.asString ?: "",
                                                email = it.get("email")?.asString ?: "",
                                                profile_photo_path = it.get("profile_photo_path")?.asString ?: "",
                                                profile_photo_url = it.get("profile_photo_url")?.asString ?: "",
                                                role = role
                                            )
                                        }
                                        _adminResult.value = adminResponse
                                        _loginResponseMessage.value = "Login Berhasil"
                                        _loginResult.value = true
                                        Log.d(TAG, "User authenticated as Admin")
                                    }
                                    "mahasiswa" -> {
                                        val mahasiswaResponse = userBody?.let {
                                            MahasiswaResponse(
                                                id = it.get("id").asInt,
                                                nama = it.get("nama")?.asString ?: "",
                                                email = it.get("email")?.asString ?: "",
                                                nim = it.get("nim")?.asString ?: "",
                                                angkatan = it.get("angkatan")?.asString ?: "",
                                                sistem_kuliah = it.get("sistem_kuliah")?.asString ?: "",
                                                gender = it.get("gender")?.asString ?: "",
                                                agama = it.get("agama")?.asString ?: "",
                                                tempat_lahir = it.get("tempat_lahir")?.asString ?: "",
                                                tanggal_lahir = it.get("tanggal_lahir")?.asString ?: "",
                                                berat_badan = it.get("berat_badan")?.asString ?: "",
                                                tinggi_badan = it.get("tinggi_badan")?.asString ?: "",
                                                golongan_darah = it.get("golongan_darah")?.asString ?: "",
                                                alamat = it.get("alamat")?.asString ?: "",
                                                nohp = it.get("nohp")?.asString ?: "",
                                                notelp = it.get("notelp")?.asString ?: "",
                                                status = it.get("status")?.asInt ?: 0,
                                                prodi_id = it.get("prodi_id")?.asInt ?: 0,
                                                kelase_id = it.get("kelase_id")?.asInt ?: 0,
                                                user_id = it.get("user_id")?.asInt ?: 0,
                                                profile_photo_path = it.get("profile_photo_path")?.asString ?: "",
                                                profile_photo_url = it.get("profile_photo_url")?.asString ?: "",
                                                role = role
                                            )
                                        }
                                        _mahasiswaResult.value = mahasiswaResponse
                                        _loginResponseMessage.value = "Login Berhasil"
                                        _loginResult.value = true
                                        Log.d(TAG, "User authenticated as Mahasiswa")
                                    }
                                    "dosen" -> {
                                        val dosenResponse = userBody?.let {
                                            DosenResponse(
                                                id = it.get("id").asInt,
                                                nama = it.get("nama")?.asString ?: "",
                                                email = it.get("email")?.asString ?: "",
                                                nidn = it.get("nidn")?.asString ?: "",
                                                nip = it.get("nip")?.asString ?: "",
                                                gender = it.get("gender")?.asString ?: "",
                                                agama = it.get("agama")?.asString ?: "",
                                                tempat_lahir = it.get("tempat_lahir")?.asString ?: "",
                                                tanggal_lahir = it.get("tanggal_lahir")?.asString ?: "",
                                                pendidikan = it.get("pendidikan")?.asString ?: "",
                                                sik = it.get("sik")?.asString ?: "",
                                                alamat = it.get("alamat")?.asString ?: "",
                                                jabatan = it.get("jabatan")?.asString ?: "",
                                                nohp = it.get("nohp")?.asString ?: "",
                                                notelp = it.get("notelp")?.asString ?: "",
                                                status = it.get("status")?.asInt ?: 0,
                                                prodi_id = it.get("prodi_id")?.asInt ?: 0,
                                                user_id = it.get("user_id")?.asInt ?: 0,
                                                profile_photo_path = it.get("profile_photo_path")?.asString ?: "",
                                                profile_photo_url = it.get("profile_photo_url")?.asString ?: "",
                                                role = role
                                            )
                                        }
                                        _dosenResult.value = dosenResponse
                                        _loginResponseMessage.value = "Login Berhasil"
                                        _loginResult.value = true
                                        Log.d(TAG, "User authenticated as Dosen")
                                    }
                                    else -> {
                                        _loginResponseMessage.value = "User tidak diautentikasi"
                                        _loginResult.value = false
                                        Log.d(TAG, "User authenticated as Error")
                                    }
                                }
                            } else {
                                _loginResponseMessage.value = "User tidak diautentikasi"
                                _loginResult.value = false
                            }
                        } catch (e: IOException) {
                            _loginResponseMessage.value = "Terjadi kesalahan jaringan"
                            _loginResult.value = false
                        } catch (e: Exception) {
                            _loginResponseMessage.value = "Terjadi kesalahan: ${e.message}"
                            _loginResult.value = false
                        }
                    } else {
                        _loginResponseMessage.value = loginResponse?.message ?: "Kesalahan tidak diketahui"
                        _loginResult.value = false
                    }
                } else {
                    _loginResponseMessage.value = "Kesalahan server: ${response.code()}"
                    _loginResult.value = false
                }
            } catch (e: Exception) {
                _loginResponseMessage.value = "Terjadi kesalahan: ${e.message}"
                _loginResult.value = false
            }
        }
    }
}
