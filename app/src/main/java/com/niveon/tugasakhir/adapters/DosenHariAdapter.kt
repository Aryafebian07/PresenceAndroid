package com.niveon.tugasakhir.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.niveon.tugasakhir.R
import com.niveon.tugasakhir.model.HariDosen
import com.niveon.tugasakhir.model.JadwalDosen

class DosenHariAdapter(private var dataHariDosen: MutableList<HariDosen>) :
    RecyclerView.Adapter<DosenHariAdapter.DosenHariViewHolder>() {

    class DosenHariViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupTitle: TextView = itemView.findViewById(R.id.txtHari)
        val recyclerViewJadwal: RecyclerView = itemView.findViewById(R.id.recyclerViewJadwal)
        val groupIndicator: ImageView = itemView.findViewById(R.id.groupIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DosenHariViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_hari_group, parent, false)
        return DosenHariViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DosenHariViewHolder, position: Int) {
        val hari = dataHariDosen[position]
        holder.groupTitle.text = hari.nama

        val jadwal = hari.jadwalDosen
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
            val emptyJadwalListDosen = mutableListOf(
                JadwalDosen("Tidak ada jadwal","","","")
            )
            setupChildRecyclerView(holder.recyclerViewJadwal, emptyJadwalListDosen, true)
            holder.groupIndicator.visibility = View.GONE
        } else {
            setupChildRecyclerView(holder.recyclerViewJadwal, jadwal, hari.isExpanded)
            holder.groupIndicator.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return dataHariDosen.size
    }

    private fun setupChildRecyclerView(
        recyclerView: RecyclerView,
        jadwalDosen: List<JadwalDosen>,
        isExpanded: Boolean
    ) {
        if (isExpanded) {
            val adapter = DosenJadwalAdapter(jadwalDosen)
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                this.adapter = adapter
            }
        } else {
            recyclerView.adapter = null
        }
    }

    fun setData(dataHariDosen: List<HariDosen>) {
        this.dataHariDosen.clear()
        this.dataHariDosen.addAll(dataHariDosen)
        notifyDataSetChanged()
    }
}



