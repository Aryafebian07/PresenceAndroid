package com.niveon.tugasakhir.model

data class HariMahasiswa(val nama: String, val jadwalMahasiswa: MutableList<JadwalMahasiswa>, var isExpanded: Boolean = false)
