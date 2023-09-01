package com.niveon.tugasakhir.model

data class AdminResponse(
    val id: Int,
    val nama: String?,
    val username: String?,
    val email: String?,
    val profile_photo_path: String?,
    val profile_photo_url: String?,
    val role: String?
)
