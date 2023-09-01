package com.niveon.tugasakhir.ui.mahasiswa.profile

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
import com.bumptech.glide.Glide
import com.niveon.tugasakhir.R
import com.niveon.tugasakhir.databinding.FragmentMahasiswaProfileBinding
import com.niveon.tugasakhir.util.Constants
import com.niveon.tugasakhir.util.SharedData

class MahasiswaProfileFragment : Fragment() {

    private lateinit var binding: FragmentMahasiswaProfileBinding
    private lateinit var viewModel: MahasiswaProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMahasiswaProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(MahasiswaProfileViewModel::class.java)
        animasi_layout()
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mahasiswaResponse = SharedData.mahasiswaResponse
        if (mahasiswaResponse != null) {
            val idMahasiswa = mahasiswaResponse.id
            viewModel.getDataMahasiswa(idMahasiswa)
        }
        viewModel.dataMahasiswaResult.observe(viewLifecycleOwner, Observer { response ->
            if (response != null && response.isSuccessful) {
                val dataMahasiswaResponse = response.body()
                if (dataMahasiswaResponse != null && dataMahasiswaResponse.has("data")) {
                    val data = dataMahasiswaResponse.getAsJsonObject("data")

                    binding.txtNamaMahasiswa.text = data.get("nama").asString
                    binding.txtNim.text = data.get("nim").asString
                    binding.txtProgramStudi.text = data.get("program_studi").asString
                    binding.txtAngkatan.text = data.get("angkatan").asString
                    binding.txtSistemKuliah.text = data.get("sistem_kuliah").asString
                    binding.txtKelas.text = data.get("kelas").asString
                    binding.txtStatusMahasiswa.text = data.get("status").asString
                    binding.txtJenisKelamin.text = data.get("gender").asString
                    binding.txtTempatLahir.text = data.get("tempat_lahir").asString
                    binding.txtTanggalLahir.text = data.get("tanggal_lahir").asString
                    binding.txtAgama.text = data.get("agama").asString
                    binding.txtBeratbadan.text = data.get("berat_badan").asString
                    binding.txtTinggiBadan.text = data.get("tinggi_badan").asString
                    binding.txtGolonganDarah.text = data.get("golongan_darah").asString
                    binding.txtNoTelepon.text = data.get("notelp").asString
                    binding.txtNoHP.text = data.get("nohp").asString
                    binding.txtEmail.text = data.get("email").asString
                    binding.txtAlamat.text = data.get("alamat").asString

                    val photoPath =  data.get("profile_photo_path").asString
                    val photoUrl = data.get("profile_photo_url").asString
                    if (!photoPath.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(Constants.FILE_URL+photoPath)
                            .into(binding.imgProfile)
                    }
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
            binding.cardView0.animation = fade_in
            binding.cardView1.animation = fade_in
            binding.cardView2.animation = fade_in
            binding.cardView3.animation = fade_in
            binding.textView.animation = fade_in
        }
        handler.postDelayed(runnable, 1000)
    }

}