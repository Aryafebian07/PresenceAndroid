package com.niveon.tugasakhir.ui.admin.home

import android.app.AlertDialog
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.niveon.tugasakhir.R
import com.niveon.tugasakhir.databinding.FragmentWriteNfcDosenBinding
import com.niveon.tugasakhir.model.Dosens

class WriteNfcDosenFragment : Fragment(), NfcAdapter.ReaderCallback {

    private lateinit var viewModel: WriteNfcDosenViewModel
    private lateinit var binding: FragmentWriteNfcDosenBinding
    private var nfcAdapter: NfcAdapter? = null
    private lateinit var alertDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWriteNfcDosenBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(WriteNfcDosenViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())
        val autoCompleteTextView: AutoCompleteTextView = binding.autoCompleteTextView
        val buttonReadNfc: Button = binding.buttonWriteNfc

        // Fetch dosens data from the endpoint using Retrofit
        viewModel.getDosenList()

        // Observe the dosenList LiveData to populate the AutoCompleteTextView
        viewModel.dosenList.observe(viewLifecycleOwner) { dosens ->
            val dosensFormatted = dosens.map { "${it.username} - ${it.nama}" } // Format: "username - nama"
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, dosensFormatted)
            autoCompleteTextView.setAdapter(adapter)
        }

        // Observe updateNfcResult to show the Toast
        viewModel.updateNfcResult.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                val message = if (response.isSuccessful) {
                    response.body()?.get("message")?.asString ?: "Data NFC Berhasil diupdate"
                } else {
                    "Data NFC Gagal diupdate"
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Gagal mengupdate data NFC", Toast.LENGTH_SHORT).show()
            }
        }

        buttonReadNfc.setOnClickListener {
            val selectedDosen = autoCompleteTextView.text.toString().trim()

            if (selectedDosen.isEmpty()) {
                Toast.makeText(requireContext(), "Pilih dosen terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Split the selectedDosen to get the username part
            val usernameOnly = selectedDosen.split("-")[0].trim()

            // Validate if the usernameOnly is in the list of available dosens
            val dosensFormatted = viewModel.dosenList.value?.map { "${it.username} - ${it.nama}" }
            if (!dosensFormatted?.contains(selectedDosen)!!) {
                Toast.makeText(requireContext(), "Pilihan dosen tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nfcAdapter == null) {
                showNfcNotSupportedDialog()
            } else if (!nfcAdapter!!.isEnabled) {
                showNfcNotEnabledDialog()
            } else {
                showNfcTapDialog(usernameOnly)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(activity)
    }

    override fun onTagDiscovered(tag: Tag?) {
        activity?.runOnUiThread {
            val tagId = tag?.id
            val tagIdHex = tagId?.joinToString("") { byte -> "%02X".format(byte) }

            binding.nfcTag = tag

            val selectedDosen = binding.autoCompleteTextView.text.toString().trim()
            val username = selectedDosen.substringBefore(" - ")
            if (selectedDosen.isNotEmpty()) {
                alertDialog.dismiss()
                // Prepare the data for the POST request

                val requestBody = JsonObject().apply {
                    // You can adjust the keys and values based on your API requirements
                    addProperty("username", username)
                    addProperty("nfcid", tagIdHex)
                }

                // Make the POST request using Retrofit
                viewModel.updateNfcData(requestBody)
            } else {
                Toast.makeText(requireContext(), "Pilih dosen terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showNfcTapDialog(selectedDosen: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_nfc_tap, null)
        val dialogImageView: ImageView = dialogView.findViewById(R.id.imageViewNfcTap)
        val dialogTextView: TextView = dialogView.findViewById(R.id.textViewNfcTap)
        alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton("Batal") { dialog, _ ->
                nfcAdapter?.disableReaderMode(activity)
                dialog.dismiss()
            }
            .create()

        dialogTextView.text = "Silakan tap atau tempelkan kartu NFC Anda di dekat perangkat untuk membaca data."

        alertDialog.show()

        // Register NFC reader callback
        nfcAdapter?.enableReaderMode(activity, this, NfcAdapter.FLAG_READER_NFC_A, null)
    }

    private fun showNfcNotEnabledDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("NFC Tidak Aktif")
            .setMessage("Aktifkan NFC untuk membaca data dari tag NFC.")
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
