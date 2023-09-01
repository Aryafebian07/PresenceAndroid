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
import com.niveon.tugasakhir.adapters.PertemuanHarianAdapter
import com.niveon.tugasakhir.databinding.FragmentMahasiswaHomeBinding
import com.niveon.tugasakhir.databinding.FragmentMahasiswaPertemuanHarianBinding
import com.niveon.tugasakhir.ui.mahasiswa.home.MahasiswaHomeViewModel
import com.niveon.tugasakhir.util.SharedData
import com.niveon.tugasakhir.util.SharedData.mahasiswaResponse

class MahasiswaPertemuanHarianFragment : Fragment() {

    private lateinit var binding: FragmentMahasiswaPertemuanHarianBinding
    private lateinit var viewModel: MahasiswaPertemuanHarianViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMahasiswaPertemuanHarianBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(MahasiswaPertemuanHarianViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val mainActivity = requireActivity() as MainActivity
        mainActivity.setToolbarTitle("Pertemuan Hari ini")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mahasiswaResponse = SharedData.mahasiswaResponse
        if (mahasiswaResponse != null) {
            val kelaseid = mahasiswaResponse.kelase_id
            if (kelaseid != null) {
                viewModel.getPertemuanHarian(kelaseid)
            }else{

            }
        }
        pertemuanharian()

    }


    fun pertemuanharian() {
        viewModel.pertemuanHarianResult.observe(viewLifecycleOwner, Observer { response ->
            if (response != null && response.isSuccessful) {
                val pertemuanHarianResponse = response.body()
                if (pertemuanHarianResponse != null && pertemuanHarianResponse.has("data")) {
                    val data = pertemuanHarianResponse.getAsJsonArray("data")
                    val pertemuanList = mutableListOf<JsonObject>()
                    data.forEach { item -> pertemuanList.add(item.asJsonObject) }

                    // Buat RecyclerView dan adapter untuk menampilkan daftar pertemuan harian
                    val recyclerView = binding.recyclerViewPertemuanNow
                    val pertemuanAdapter = PertemuanHarianAdapter(pertemuanList, requireFragmentManager()) // Pass the FragmentManager here
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    recyclerView.adapter = pertemuanAdapter
                    binding.clPertemuanHarian.visibility = View.VISIBLE
                    binding.clPertemuanKosong.visibility = View.GONE

                } else {
                    binding.clPertemuanHarian.visibility = View.GONE
                    binding.clPertemuanKosong.visibility = View.VISIBLE
                    // TODO: Handle empty pertemuan harian data
                }
            } else {
                binding.clPertemuanHarian.visibility = View.GONE
                binding.clPertemuanKosong.visibility = View.VISIBLE
                // TODO: Handle pertemuan harian request failure
            }
        })
    }
}