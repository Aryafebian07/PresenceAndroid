package com.niveon.tugasakhir.ui.admin.home

import android.app.AlertDialog
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.niveon.tugasakhir.databinding.FragmentWriteNfcAdminBinding

class WriteNfcAdminFragment : Fragment(), NfcAdapter.ReaderCallback {

    private lateinit var viewModel: WriteNfcAdminViewModel
    private lateinit var binding: FragmentWriteNfcAdminBinding
    private var nfcAdapter: NfcAdapter? = null
    private lateinit var alertDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWriteNfcAdminBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(WriteNfcAdminViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())
        val editTextDataToWrite: EditText = binding.editTextDataToWrite
        val buttonWriteNfc: Button = binding.buttonWriteNfc

        buttonWriteNfc.setOnClickListener {
            val messageToWrite = editTextDataToWrite.text.toString().trim()

            if (messageToWrite.isEmpty()) {
                Toast.makeText(requireContext(), "Masukkan data untuk ditulis", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nfcAdapter == null) {
                showNfcNotSupportedDialog()
            } else if (!nfcAdapter!!.isEnabled) {
                showNfcNotEnabledDialog()
            } else {
                viewModel.resetWriteResult()
                showNfcTapDialog(messageToWrite)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(activity)
    }

    override fun onTagDiscovered(tag: Tag?) {
        activity?.runOnUiThread {
            binding.nfcTag = tag

            val messageToWrite = binding.editTextDataToWrite.text.toString().trim()
            if (messageToWrite.isNotEmpty()) {
                viewModel.writeTag(tag, messageToWrite)
                alertDialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Masukkan data untuk ditulis", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showNfcTapDialog(messageToWrite: String) {
        alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Tap atau Tempelkan Kartu NFC")
            .setMessage("Silakan tap atau tempelkan kartu NFC Anda di dekat perangkat untuk menulis data.")
            .setCancelable(false)
            .setPositiveButton("Batal") { dialog, _ ->
                nfcAdapter?.disableReaderMode(activity)
                dialog.dismiss()
            }
            .create()

        alertDialog.show()

        // Daftarkan callback pembaca NFC
        nfcAdapter?.enableReaderMode(activity, this, NfcAdapter.FLAG_READER_NFC_A, null)

        // Amati writeResult untuk mendapatkan status penulisan
        viewModel.writeResult.observe(viewLifecycleOwner) { success ->
            Log.d("NFC", "Status penulisan NFC: $success")
            if (success == true) {
                alertDialog.dismiss()
                nfcAdapter?.disableReaderMode(activity)
                Toast.makeText(requireContext(), "Tulisan NFC berhasil", Toast.LENGTH_SHORT).show()
            } else if(success == false) {
                Toast.makeText(requireContext(), "Tulisan NFC gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showNfcNotEnabledDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("NFC Tidak Aktif")
            .setMessage("Aktifkan NFC untuk menulis data ke tag NFC.")
            .setPositiveButton("Pergi ke Pengaturan NFC") { dialog, _ ->
                startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showNfcNotSupportedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("NFC Tidak Didukung")
            .setMessage("Perangkat ini tidak mendukung NFC.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
