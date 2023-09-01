package com.niveon.tugasakhir.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.niveon.tugasakhir.R
import com.niveon.tugasakhir.ui.mahasiswa.pertemuan.MahasiswaPertemuanDetailFragment

class MahasiswaPertemuanRiwayatAdapter(private var _data: List<JsonObject>,private val fragmentManager: FragmentManager) :
    RecyclerView.Adapter<MahasiswaPertemuanRiwayatAdapter.MahasiswaPertemuanRiwayatViewHolder>() {

    var data: List<JsonObject>
        get() = _data
        set(value) {
            _data = value
        }

    class MahasiswaPertemuanRiwayatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNamaMataKuliah: TextView = itemView.findViewById(R.id.txtNamaJadwal)
        val txtJam: TextView = itemView.findViewById(R.id.txtJam)
        val txtPertemuanKe: TextView = itemView.findViewById(R.id.txtPertemuanKeb)
        val txtStatus: TextView = itemView.findViewById(R.id.txtStatuse)
        val cvItem : CardView = itemView.findViewById(R.id.cvPertemuanItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MahasiswaPertemuanRiwayatViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_pertemuan_mahasiswa_riwayat_item, parent, false)
        return MahasiswaPertemuanRiwayatViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MahasiswaPertemuanRiwayatViewHolder, position: Int) {
        val item = data[position].getAsJsonObject()
        holder.txtNamaMataKuliah.text = item.get("nama").asString
        holder.txtJam.text = "${ item.get("jam_mulai").asString } - ${ item.get("jam_selesai").asString }"
        holder.txtPertemuanKe.text = "Pertemuan ${item.get("ke").asString}"
        holder.txtStatus.text = item.get("status").asString
        holder.cvItem.setOnClickListener {
            val pertemuanId = item.get("id").asInt
            val fragment = MahasiswaPertemuanDetailFragment.newInstance(pertemuanId)
            fragmentManager?.beginTransaction()
                ?.replace(R.id.frame_layout, fragment)
                ?.addToBackStack(null)
                ?.commit()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
