package com.niveon.tugasakhir.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.niveon.tugasakhir.R
import com.niveon.tugasakhir.model.HariMahasiswa
import com.niveon.tugasakhir.model.JadwalMahasiswa

class MahasiswaHariAdapter(private var dataHariMahasiswa: MutableList<HariMahasiswa>) :
    RecyclerView.Adapter<MahasiswaHariAdapter.HariViewHolder>() {

    class HariViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupTitle: TextView = itemView.findViewById(R.id.txtHari)
        val recyclerViewJadwal: RecyclerView = itemView.findViewById(R.id.recyclerViewJadwal)
        val groupIndicator: ImageView = itemView.findViewById(R.id.groupIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HariViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_hari_group, parent, false)
        return HariViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HariViewHolder, position: Int) {
        val hari = dataHariMahasiswa[position]
        holder.groupTitle.text = hari.nama

        val jadwal = hari.jadwalMahasiswa
        val isJadwalEmpty = jadwal.isEmpty()

        holder.groupIndicator.setImageResource(
            if (hari.isExpanded) R.drawable.ic_round_arrow_drop_up else R.drawable.ic_round_arrow_drop_down
        )

        holder.itemView.setOnClickListener {
            hari.isExpanded = !hari.isExpanded
            notifyDataSetChanged()
        }

        if (isJadwalEmpty && hari.isExpanded) {
            // Jika jadwal kosong dan group di-expand, tampilkan pesan "Tidak ada jadwal"
            val emptyJadwalListMahasiswa = mutableListOf(
                JadwalMahasiswa("Tidak ada jadwal","","",""))
            setupChildRecyclerView(holder.recyclerViewJadwal, emptyJadwalListMahasiswa, true)
            holder.groupIndicator.visibility = View.GONE
        } else {
            setupChildRecyclerView(holder.recyclerViewJadwal, jadwal, hari.isExpanded)
            holder.groupIndicator.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return dataHariMahasiswa.size
    }

    private fun setupChildRecyclerView(
        recyclerView: RecyclerView,
        jadwalMahasiswa: List<JadwalMahasiswa>,
        isExpanded: Boolean
    ) {
        if (isExpanded) {
            val adapter = MahasiswaJadwalAdapter(jadwalMahasiswa)
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                this.adapter = adapter
            }
        } else {
            recyclerView.adapter = null
        }
    }

    fun setData(dataHariMahasiswa: List<HariMahasiswa>) {
        this.dataHariMahasiswa.clear()
        this.dataHariMahasiswa.addAll(dataHariMahasiswa)
        notifyDataSetChanged()
    }
}



