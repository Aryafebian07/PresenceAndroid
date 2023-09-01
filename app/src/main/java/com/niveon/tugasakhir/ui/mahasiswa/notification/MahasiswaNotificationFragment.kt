package com.niveon.tugasakhir.ui.mahasiswa.notification

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.niveon.tugasakhir.R

class MahasiswaNotificationFragment : Fragment() {

    companion object {
        fun newInstance() = MahasiswaNotificationFragment()
    }

    private lateinit var viewModel: MahasiswaNotificationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mahasiswa_notification, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MahasiswaNotificationViewModel::class.java)
        // TODO: Use the ViewModel
    }

}