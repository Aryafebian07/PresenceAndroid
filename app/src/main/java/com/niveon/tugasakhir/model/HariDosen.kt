package com.niveon.tugasakhir.model

data class HariDosen(val nama: String, val jadwalDosen: MutableList<JadwalDosen>, var isExpanded: Boolean = false)
