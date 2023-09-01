package com.niveon.tugasakhir.ui.mahasiswa.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.niveon.tugasakhir.R
import com.niveon.tugasakhir.adapters.PertemuanHarianAdapter
import com.niveon.tugasakhir.databinding.FragmentMahasiswaHomeBinding
import com.niveon.tugasakhir.ui.mahasiswa.jadwal.MahasiswaJadwalFragment
import com.niveon.tugasakhir.ui.mahasiswa.pertemuan.MahasiswaPertemuanDetailFragment
import com.niveon.tugasakhir.ui.mahasiswa.pertemuan.MahasiswaPertemuanHarianFragment
import com.niveon.tugasakhir.ui.mahasiswa.pertemuan.MahasiswaPertemuanRiwayatFragment
import com.niveon.tugasakhir.ui.viewmodel.SharedViewModel
import com.niveon.tugasakhir.util.Constants.FILE_URL
import com.niveon.tugasakhir.util.SharedData

class MahasiswaHomeFragment : Fragment() {

    private lateinit var binding: FragmentMahasiswaHomeBinding
    private lateinit var viewModel: MahasiswaHomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMahasiswaHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(MahasiswaHomeViewModel::class.java)
        animasi_layout()
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mahasiswaResponse = SharedData.mahasiswaResponse
        if (mahasiswaResponse != null) {
            val namaMahasiswa = mahasiswaResponse.nama
            binding.txtNamaMahasiswa.text = namaMahasiswa

            if (namaMahasiswa!!.length > 15) {
                // Hitung lebar teks txtNamaMahasiswa
                val txtNamaMahasiswaWidth = binding.txtNamaMahasiswa.paint.measureText(namaMahasiswa)

                // Set margin kiri untuk imgProfile berdasarkan lebar teks txtNamaMahasiswa
                val layoutParams = binding.imgProfile.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.marginStart = txtNamaMahasiswaWidth.toInt()
                binding.imgProfile.layoutParams = layoutParams
            }

            val photoPath = mahasiswaResponse.profile_photo_path
            val photoUrl = mahasiswaResponse.profile_photo_url

            if (!photoPath.isNullOrEmpty()) {
                Glide.with(this)
                    .load(FILE_URL+photoPath)
                    .into(binding.imgProfile)
            }

            val kelaseid = mahasiswaResponse.kelase_id
            if (kelaseid != null) {
                viewModel.getPertemuanBerlangsung(kelaseid)
            }else{

            }
        }
        pertemuanberlangsung()
        binding.btnJadwal.setOnClickListener {
            val fragment = MahasiswaJadwalFragment()
            fragmentManager?.beginTransaction()
                ?.replace(R.id.frame_layout, fragment)
                ?.addToBackStack(null)
                ?.commit()
        }
        binding.btnPertemuanHarian.setOnClickListener {
            val fragment = MahasiswaPertemuanHarianFragment()
            fragmentManager?.beginTransaction()
                ?.replace(R.id.frame_layout, fragment)
                ?.addToBackStack(null)
                ?.commit()
        }

        binding.btnRiwayatPertemuan.setOnClickListener {
            val fragment = MahasiswaPertemuanRiwayatFragment()
            fragmentManager?.beginTransaction()
                ?.replace(R.id.frame_layout, fragment)
                ?.addToBackStack(null)
                ?.commit()
        }
    }

    fun pertemuanberlangsung(){
        viewModel.pertemuanBerlangsungResult.observe(viewLifecycleOwner, Observer{ response->
            if(response != null && response.isSuccessful){
                val pertemuanBerlangsungResponse = response.body()
                if (pertemuanBerlangsungResponse != null && pertemuanBerlangsungResponse.has("data")){
                    val data = pertemuanBerlangsungResponse.getAsJsonObject("data")
                    binding.txtNamaJadwal.text = data.get("nama").asString
                    binding.txtJamMulai.text = data.get("jam_mulai").asString
                    binding.txtJamBerakhir.text = data.get("jam_selesai").asString
                    binding.clBerlangsungAda.visibility = View.VISIBLE

                    binding.cvPertemuanBerlangsung.setOnClickListener{
                        val pertemuanId = data.get("id").asInt
                        val fragment = MahasiswaPertemuanDetailFragment.newInstance(pertemuanId)
                        fragmentManager?.beginTransaction()
                            ?.replace(R.id.frame_layout, fragment)
                            ?.addToBackStack(null)
                            ?.commit()
                    }
                }else{
                    val message = pertemuanBerlangsungResponse?.get("message")?.asString
                    binding.txtKosongJadwal.text = message
                    binding.clBerlangsungKosong.visibility = View.VISIBLE
                }
            }

        })
    }

    private fun animasi_layout(){
        val fade_in: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        val bottom_down: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.bottom_down)

        binding.topLinearLayout.animation = bottom_down
        val handler = Handler()
        val runnable = Runnable {
            binding.hello.animation = fade_in
            binding.txtNamaMahasiswa.animation = fade_in
            binding.imgProfile.animation = fade_in
            binding.cvPertemuanBerlangsung.animation = fade_in
            binding.menuList.animation = fade_in
        }
        handler.postDelayed(runnable, 1000)
    }
}