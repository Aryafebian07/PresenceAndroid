package com.niveon.tugasakhir.ui.dosen.pertemuan

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.niveon.tugasakhir.R

class DosenPertemuanHarianFragment : Fragment() {

    companion object {
        fun newInstance() = DosenPertemuanHarianFragment()
    }

    private lateinit var viewModel: DosenPertemuanHarianViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dosen_pertemuan_harian, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DosenPertemuanHarianViewModel::class.java)
        // TODO: Use the ViewModel
    }

}