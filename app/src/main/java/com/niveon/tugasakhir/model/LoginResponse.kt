package com.niveon.tugasakhir.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginData
)
data class LoginData(
    val token: String,
    val id: Int,
    val nama: String,
    val role: String
)
