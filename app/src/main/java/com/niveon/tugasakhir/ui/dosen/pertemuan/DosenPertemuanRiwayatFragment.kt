package com.niveon.tugasakhir.ui.dosen.pertemuan

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.niveon.tugasakhir.R

class DosenPertemuanRiwayatFragment : Fragment() {

    companion object {
        fun newInstance() = DosenPertemuanRiwayatFragment()
    }

    private lateinit var viewModel: DosenPertemuanRiwayatViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dosen_pertemuan_riwayat, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DosenPertemuanRiwayatViewModel::class.java)
        // TODO: Use the ViewModel
    }

}