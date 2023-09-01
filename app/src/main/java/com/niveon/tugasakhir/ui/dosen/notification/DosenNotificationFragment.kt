package com.niveon.tugasakhir.ui.dosen.notification

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.niveon.tugasakhir.R

class DosenNotificationFragment : Fragment() {

    companion object {
        fun newInstance() = DosenNotificationFragment()
    }

    private lateinit var viewModel: DosenNotificationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dosen_notification, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DosenNotificationViewModel::class.java)
        // TODO: Use the ViewModel
    }

}