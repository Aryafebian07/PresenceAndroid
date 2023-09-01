package com.niveon.tugasakhir.ui.dosen.jadwal

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.niveon.tugasakhir.adapters.DosenHariAdapter
import com.niveon.tugasakhir.databinding.FragmentDosenJadwalBinding
import com.niveon.tugasakhir.model.HariDosen
import com.niveon.tugasakhir.model.JadwalDosen
import com.niveon.tugasakhir.ui.viewmodel.SharedViewModel
import com.niveon.tugasakhir.util.SharedData

class DosenJadwalFragment : Fragment() {

    private lateinit var binding: FragmentDosenJadwalBinding
    private lateinit var hariAdapter: DosenHariAdapter
    private lateinit var viewModel: DosenJadwalViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDosenJadwalBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(DosenJadwalViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dosenResponse = SharedData.dosenResponse
        if (dosenResponse != null) {
            val dosenid = dosenResponse.id
            if (dosenid != null) {
                viewModel.getJadwalList(dosenid)
            }else{

            }
        }

        setupRecyclerView()
        viewModel.jadwalResult.observe(viewLifecycleOwner, Observer { response ->
            if (response != null && response.isSuccessful) {
                val jadwalResponse = response.body()
                if (jadwalResponse != null && jadwalResponse.has("data")) {
                    val jadwalArray = jadwalResponse.getAsJsonArray("data")
                    val dataHariDosen = mutableListOf<HariDosen>()

                    // Hard code data hari
                    val listHari = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")

                    // Inisialisasi dataHari dengan hari dan jadwal kosong
                    listHari.forEach { hari ->
                        dataHariDosen.add(HariDosen(hari, mutableListOf()))
                    }

                    // Group jadwal berdasarkan hari
                    jadwalArray.forEach { dataElement ->
                        if (dataElement.isJsonObject) {
                            val jadwalObject = dataElement.asJsonObject
                            val hari = jadwalObject.get("hari").asString
                            val jadwalNama = jadwalObject.get("nama").asString
                            val jamMulai = jadwalObject.get("jam_mulai").asString
                            val jamSelesai = jadwalObject.get("jam_selesai").asString
                            val dosen = jadwalObject.get("dosen").asString
                            val kelas = jadwalObject.get("kelas").asString

                            val jadwallist = JadwalDosen(
                                jadwalNama,jamMulai,jamSelesai,kelas
                            )

                            // Cari dataHari yang sesuai berdasarkan hari dan tambahkan jadwal
                            val existingHari = dataHariDosen.find { it.nama == hari }
                            existingHari?.jadwalDosen?.add(jadwallist)
                        }
                    }

                    // Update data dan notifikasi adapter
                    hariAdapter.setData(dataHariDosen)
                }
            } else {
                // Handle error case if necessary
            }
        })
    }
    private fun setupRecyclerView() {
        hariAdapter = DosenHariAdapter(mutableListOf())
        binding.recyclerViewHari.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = hariAdapter
        }
    }
}