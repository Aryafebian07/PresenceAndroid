package com.niveon.tugasakhir.ui.admin.profile

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
import com.niveon.tugasakhir.databinding.FragmentAdminProfileBinding
import com.niveon.tugasakhir.util.Constants
import com.niveon.tugasakhir.util.SharedData

class AdminProfileFragment : Fragment() {
    private lateinit var binding: FragmentAdminProfileBinding
    private lateinit var viewModel: AdminProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(AdminProfileViewModel::class.java)
        animasi_layout()
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)






        val adminResponse = SharedData.adminResponse
        if (adminResponse != null) {
            binding.txtNamaAdmin.text = adminResponse.nama
            binding.txtUsername.text = adminResponse.username
            binding.txtEmail.text = adminResponse.email


            val photoPath = adminResponse.profile_photo_path
            val photoUrl = adminResponse.profile_photo_url

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
            binding.textView.animation = fade_in
        }
        handler.postDelayed(runnable, 1000)
    }
}