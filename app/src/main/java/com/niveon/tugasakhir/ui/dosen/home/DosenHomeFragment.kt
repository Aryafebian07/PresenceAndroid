package com.niveon.tugasakhir.ui.dosen.home

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
import com.niveon.tugasakhir.databinding.FragmentDosenHomeBinding
import com.niveon.tugasakhir.ui.dosen.jadwal.DosenJadwalFragment
import com.niveon.tugasakhir.ui.dosen.pertemuan.DosenPertemuanDetailFragment
import com.niveon.tugasakhir.ui.dosen.pertemuan.DosenPertemuanHarianFragment
import com.niveon.tugasakhir.ui.dosen.pertemuan.DosenPertemuanRiwayatFragment
import com.niveon.tugasakhir.util.Constants
import com.niveon.tugasakhir.util.SharedData

class DosenHomeFragment : Fragment() {

    private lateinit var binding: FragmentDosenHomeBinding
    private lateinit var viewModel: DosenHomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDosenHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(DosenHomeViewModel::class.java)
        animasi_layout()
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dosenResponse = SharedData.dosenResponse
        if (dosenResponse != null) {
            val namaDosen = dosenResponse.nama
            binding.txtNamaDosen.text = namaDosen

            if (namaDosen!!.length > 15) {
                // Hitung lebar teks txtNamaDosen
                val txtNamaDosenWidth = binding.txtNamaDosen.paint.measureText(namaDosen)

                // Set margin kiri untuk imgProfile berdasarkan lebar teks txtNamaDosen
                val layoutParams = binding.imgProfile.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.marginStart = txtNamaDosenWidth.toInt()
                binding.imgProfile.layoutParams = layoutParams
            }

            val photoPath = dosenResponse.profile_photo_path
            val photoUrl = dosenResponse.profile_photo_url

            if (!photoPath.isNullOrEmpty()) {
                Glide.with(this)
                    .load(Constants.FILE_URL +photoPath)
                    .into(binding.imgProfile)
            }

            val dosenid = dosenResponse.id
            if (dosenid != null) {
                viewModel.getPertemuanBerlangsung(dosenid)
            }else{

            }
        }
        pertemuanberlangsung()
        binding.btnJadwal.setOnClickListener {
            val fragment = DosenJadwalFragment()
            fragmentManager?.beginTransaction()
                ?.replace(R.id.frame_layout, fragment)
                ?.addToBackStack(null)
                ?.commit()
        }
        binding.btnPertemuanHarian.setOnClickListener {
            val fragment = DosenPertemuanHarianFragment()
            fragmentManager?.beginTransaction()
                ?.replace(R.id.frame_layout, fragment)
                ?.addToBackStack(null)
                ?.commit()
        }

        binding.btnRiwayatPertemuan.setOnClickListener {
            val fragment = DosenPertemuanRiwayatFragment()
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
                        val fragment = DosenPertemuanDetailFragment.newInstance(pertemuanId)
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
            binding.txtNamaDosen.animation = fade_in
            binding.imgProfile.animation = fade_in
            binding.cvPertemuanBerlangsung.animation = fade_in
            binding.menuList.animation = fade_in
        }
        handler.postDelayed(runnable, 1000)
    }
}