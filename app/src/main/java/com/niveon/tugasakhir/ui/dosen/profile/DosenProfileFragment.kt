package com.niveon.tugasakhir.ui.dosen.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.bumptech.glide.Glide
import com.niveon.tugasakhir.R
import com.niveon.tugasakhir.databinding.FragmentDosenProfileBinding
import com.niveon.tugasakhir.util.Constants
import com.niveon.tugasakhir.util.SharedData

class DosenProfileFragment : Fragment() {
    private lateinit var binding: FragmentDosenProfileBinding
    private lateinit var viewModel: DosenProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDosenProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(DosenProfileViewModel::class.java)
        animasi_layout()
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)






        val dosenResponse = SharedData.dosenResponse
        if (dosenResponse != null) {
            binding.txtNamaDosen.text = dosenResponse.nama
            binding.txtNidn.text = dosenResponse.nidn
            binding.txtNip.text = dosenResponse.nip
            var sik = ""
            if(dosenResponse.sik == "1"){
                sik ="Dosen Tetap"
            }else if(dosenResponse.sik == "2"){
                sik ="Dosen Tidak Tetap"
            }else if(dosenResponse.sik == "3"){
                sik ="Dosen Honorer"
            }
            binding.txtSik.text = sik
            binding.txtJabatan.text = dosenResponse.jabatan
            binding.txtJenisKelamin.text = dosenResponse.gender
            binding.txtTempatLahir.text = dosenResponse.tempat_lahir
            binding.txtTanggalLahir.text = dosenResponse.tanggal_lahir
            binding.txtAgama.text = dosenResponse.agama
            binding.txtPendidikanTerakhir.text = dosenResponse.pendidikan
            binding.txtEmail.text = dosenResponse.email
            binding.txtNoHP.text = dosenResponse.nohp
            binding.txtNoTelepon.text = dosenResponse.notelp
            binding.txtAlamat.text = dosenResponse.alamat
            var status = if(dosenResponse.status == 0){"Aktif"}else{"Tidak Aktif"}
            binding.txtStatusDosen.text = status

            val photoPath = dosenResponse.profile_photo_path
            val photoUrl = dosenResponse.profile_photo_url

            if (!photoPath.isNullOrEmpty()) {
                Glide.with(this)
                    .load(Constants.FILE_URL +photoPath)
                    .into(binding.imgProfile)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
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