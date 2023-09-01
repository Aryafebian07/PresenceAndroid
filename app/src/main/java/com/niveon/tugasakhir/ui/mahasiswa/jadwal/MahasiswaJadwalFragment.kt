package com.niveon.tugasakhir.ui.mahasiswa.jadwal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.niveon.tugasakhir.MainActivity
import com.niveon.tugasakhir.adapters.MahasiswaHariAdapter
import com.niveon.tugasakhir.databinding.FragmentMahasiswaJadwalBinding
import com.niveon.tugasakhir.model.HariMahasiswa
import com.niveon.tugasakhir.model.JadwalMahasiswa
import com.niveon.tugasakhir.ui.viewmodel.SharedViewModel
import com.niveon.tugasakhir.util.SharedData

class MahasiswaJadwalFragment : Fragment() {

    private lateinit var binding: FragmentMahasiswaJadwalBinding
    private lateinit var mahasiswaHariAdapter: MahasiswaHariAdapter
    private lateinit var viewModel: MahasiswaJadwalViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMahasiswaJadwalBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(MahasiswaJadwalViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val mainActivity = requireActivity() as MainActivity
        mainActivity.setToolbarTitle("Jadwal")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mahasiswaResponse = SharedData.mahasiswaResponse
        if (mahasiswaResponse != null) {
            val kelaseid = mahasiswaResponse.kelase_id
            if (kelaseid != null) {
                viewModel.getJadwalList(kelaseid)
            }else{

            }
        }

        setupRecyclerView()
        viewModel.jadwalResult.observe(viewLifecycleOwner, Observer { response ->
            Log.d("MahasiswaJadwalFragment", "jadwalViewModel observe triggered")
            if (response != null && response.isSuccessful) {
                val jadwalResponse = response.body()
                if (jadwalResponse != null && jadwalResponse.has("data")) {
                    val jadwalArray = jadwalResponse.getAsJsonArray("data")
                    val dataHariMahasiswa = mutableListOf<HariMahasiswa>()

                    // Hard code data hari
                    val listHari = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")

                    // Inisialisasi dataHari dengan hari dan jadwal kosong
                    listHari.forEach { hari ->
                        dataHariMahasiswa.add(HariMahasiswa(hari, mutableListOf()))
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


                            val jadwallist = JadwalMahasiswa(
                                jadwalNama,jamMulai,jamSelesai,dosen
                            )

                            // Cari dataHari yang sesuai berdasarkan hari dan tambahkan jadwal
                            val existingHari = dataHariMahasiswa.find { it.nama == hari }
                            existingHari?.jadwalMahasiswa?.add(jadwallist)
                        }
                    }

                    // Update data dan notifikasi adapter
                    mahasiswaHariAdapter.setData(dataHariMahasiswa)
                }
            } else {
                // Handle error case if necessary
            }
        })
    }
    private fun setupRecyclerView() {
        mahasiswaHariAdapter = MahasiswaHariAdapter(mutableListOf()) // Gunakan emptyList() karena data awal belum ada
        binding.recyclerViewHari.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mahasiswaHariAdapter
        }
    }
}



