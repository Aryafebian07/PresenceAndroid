package com.niveon.tugasakhir.model

data class MahasiswaResponse(
    val id: Int,
    val nama: String?,
    val email: String?,
    val nim: String?,
    val angkatan: String?,
    val sistem_kuliah: String?,
    val gender: String?,
    val agama: String?,
    val tempat_lahir: String?,
    val tanggal_lahir: String?,
    val berat_badan: String?,
    val tinggi_badan: String?,
    val golongan_darah: String?,
    val alamat: String?,
    val nohp: String?,
    val notelp: String?,
    val status: Int?,
    val prodi_id: Int?,
    val kelase_id: Int?,
    val user_id: Int?,
    val profile_photo_path: String?,
    val profile_photo_url: String?,
    val role: String?
)
//{
//    fun getID(): Int {
//        return id
//    }
//    fun getNama(): String? {
//        return nama
//    }
//    fun getEmail(): String? {
//        return email
//    }
//    fun getNim(): String? {
//        return nim
//    }
//    fun getAngkatan(): String? {
//        return angkatan
//    }
//    fun getSistemKuliah(): String? {
//        return sistem_kuliah
//    }
//    fun getGender(): String? {
//        return gender
//    }
//    fun getAgama(): String? {
//        return agama
//    }
//    fun getTempatLahir(): String? {
//        return tempat_lahir
//    }
//    fun getTanggalLahir(): String? {
//        return tanggal_lahir
//    }
//    fun getBeratBadan(): String? {
//        return berat_badan
//    }
//    fun getTinggiBadan(): String? {
//        return tinggi_badan
//    }
//    fun getGolonganDarah(): String? {
//        return golongan_darah
//    }
//
//    fun getAlamat(): String? {
//        return alamat
//    }
//    fun getNoHp(): String? {
//        return nohp
//    }
//    fun getStatus(): Int? {
//        return status
//    }
//    fun getProdiID(): Int? {
//        return prodi_id
//    }
//    fun getKelaseID(): Int? {
//        return kelase_id
//    }
//    fun getUserID(): Int? {
//        return user_id
//    }
//    fun getProfilePath(): String? {
//        return profile_photo_path
//    }
//    fun getProfileUrl(): String? {
//        return profile_photo_url
//    }
//    fun getRole(): String? {
//        return role
//    }
//
//}