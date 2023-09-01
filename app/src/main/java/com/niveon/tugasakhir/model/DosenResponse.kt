package com.niveon.tugasakhir.model

data class DosenResponse (
    val id: Int,
    val nama: String?,
    val email: String?,
    val nidn: String?,
    val nip: String?,
    val gender: String?,
    val agama: String?,
    val tempat_lahir: String?,
    val tanggal_lahir: String?,
    val pendidikan: String?,
    val sik: String?,
    val alamat: String?,
    val jabatan: String?,
    val nohp: String?,
    val notelp: String?,
    val status: Int,
    val prodi_id: Int,
    val user_id: Int,
    val profile_photo_path: String?,
    val profile_photo_url: String?,
    val role: String?
)