package com.niveon.tugasakhir.ui.mahasiswa.pertemuan

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.niveon.tugasakhir.MainActivity
import com.niveon.tugasakhir.R
import com.niveon.tugasakhir.adapters.MahasiswaPertemuanRiwayatAdapter
import com.niveon.tugasakhir.databinding.FragmentMahasiswaPertemuanRiwayatBinding
import com.niveon.tugasakhir.util.SharedData

class MahasiswaPertemuanRiwayatFragment : Fragment() {

    private lateinit var binding: FragmentMahasiswaPertemuanRiwayatBinding
    private lateinit var viewModel: MahasiswaPertemuanRiwayatViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMahasiswaPertemuanRiwayatBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(MahasiswaPertemuanRiwayatViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val mainActivity = requireActivity() as MainActivity
        mainActivity.setToolbarTitle("Riwayat Pertemuan")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mahasiswaResponse = SharedData.mahasiswaResponse
        if (mahasiswaResponse != null) {
            val kelaseid = mahasiswaResponse.kelase_id
            if (kelaseid != null) {
                viewModel.getPertemuanRiwayat(kelaseid)
            }else{

            }
        }
        pertemuanRiwayat()

    }


    fun pertemuanRiwayat() {
        viewModel.pertemuanRiwayatResult.observe(viewLifecycleOwner, Observer { response ->
            if (response != null && response.isSuccessful) {
                val pertemuanRiwayatResponse = response.body()
                if (pertemuanRiwayatResponse != null && pertemuanRiwayatResponse.has("data")) {
                    val data = pertemuanRiwayatResponse.getAsJsonArray("data")
                    val pertemuanList = mutableListOf<JsonObject>()
                    data.forEach { item -> pertemuanList.add(item.asJsonObject) }

                    // Buat RecyclerView dan adapter untuk menampilkan daftar pertemuan Riwayat
                    val recyclerView = binding.recyclerViewPertemuanRiwayat
                    val pertemuanAdapter = MahasiswaPertemuanRiwayatAdapter(pertemuanList, requireFragmentManager()) // Pass the FragmentManager here
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    recyclerView.adapter = pertemuanAdapter
                    binding.clPertemuanRiwayat.visibility = View.VISIBLE
                    binding.clPertemuanKosong.visibility = View.GONE

                } else {
                    binding.clPertemuanRiwayat.visibility = View.GONE
                    binding.clPertemuanKosong.visibility = View.VISIBLE
                    // TODO: Handle empty pertemuan Riwayat data
                }
            } else {
                binding.clPertemuanRiwayat.visibility = View.GONE
                binding.clPertemuanKosong.visibility = View.VISIBLE
                // TODO: Handle pertemuan Riwayat request failure
            }
        })
    }

}