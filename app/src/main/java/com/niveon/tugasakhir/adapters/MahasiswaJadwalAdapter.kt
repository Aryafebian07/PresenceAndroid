package com.niveon.tugasakhir.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.niveon.tugasakhir.R
import com.niveon.tugasakhir.model.JadwalMahasiswa

// JadwalAdapter.kt
class MahasiswaJadwalAdapter(private var dataJadwalMahasiswa: List<JadwalMahasiswa>) :
    RecyclerView.Adapter<MahasiswaJadwalAdapter.JadwalViewHolder>() {

    class JadwalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNamaJadwal: TextView = itemView.findViewById(R.id.txtNamaJadwal)
        val txtJam: TextView = itemView.findViewById(R.id.txtJam)
        val txtDosen: TextView = itemView.findViewById(R.id.txtDosen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JadwalViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_jadwal_mahasiswa_item, parent, false)
        return JadwalViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: JadwalViewHolder, position: Int) {
        val jadwal = dataJadwalMahasiswa[position]
        holder.txtNamaJadwal.text = jadwal.nama
        holder.txtJam.text = "${jadwal.jamMulai} - ${jadwal.jamSelesai}"
        holder.txtDosen.text = "${jadwal.dosen}"
        if (jadwal.nama != "Tidak ada jadwal"){
            holder.txtDosen.visibility = View.VISIBLE
            holder.txtJam.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return dataJadwalMahasiswa.size
    }
}


