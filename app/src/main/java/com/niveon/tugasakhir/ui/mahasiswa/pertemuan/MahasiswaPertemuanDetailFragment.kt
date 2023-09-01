package com.niveon.tugasakhir.ui.mahasiswa.pertemuan

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.JsonObject
import com.niveon.tugasakhir.MainActivity
import com.niveon.tugasakhir.R
import com.niveon.tugasakhir.databinding.FragmentMahasiswaPertemuanDetailBinding
import com.niveon.tugasakhir.util.SharedData

class MahasiswaPertemuanDetailFragment : Fragment(), NfcAdapter.ReaderCallback, LocationListener {

    private lateinit var binding: FragmentMahasiswaPertemuanDetailBinding
    private lateinit var viewModel: MahasiswaPertemuanDetailViewModel
    private var nfcAdapter: NfcAdapter? = null
    private lateinit var alertDialog: AlertDialog
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationManager: LocationManager? = null
    private var isLocationRequested = false
    private var progressDialog: ProgressDialog? = null

    private var longitude: Double = 0.0
    private var latitude: Double = 0.0

    companion object {
        private const val ARG_PERTEMUAN_ID = "pertemuan_id"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val PERMISSION_REQUEST_READ_PHONE_STATE = 1002

        fun newInstance(pertemuanId: Int): MahasiswaPertemuanDetailFragment {
            val fragment = MahasiswaPertemuanDetailFragment()
            val args = Bundle()
            args.putInt(ARG_PERTEMUAN_ID, pertemuanId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMahasiswaPertemuanDetailBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(MahasiswaPertemuanDetailViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val mainActivity = requireActivity() as MainActivity
        mainActivity.setToolbarTitle("Detail Pertemuan")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        requestReadPhoneStatePermission()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())
        val pertemuanId = arguments?.getInt(ARG_PERTEMUAN_ID)

        if (pertemuanId != null) {
            val mahasiswaResponse = SharedData.mahasiswaResponse
            if (mahasiswaResponse != null) {
                val idMahasiswa = mahasiswaResponse.id
                viewModel.getPertemuanDetail(pertemuanId,idMahasiswa)
            }
        }

        viewModel.pertemuanDetailResult.observe(viewLifecycleOwner, Observer { response ->
            if (response != null && response.isSuccessful) {
                val pertemuanDetailResponse = response.body()
                if (pertemuanDetailResponse != null && pertemuanDetailResponse.has("data")) {
                    val data = pertemuanDetailResponse.getAsJsonObject("data")
                    if(data.has("pertemuan")){
                        val dataPertemuan = data.getAsJsonObject("pertemuan")
                        binding.txtNamaMatakuliah.text = dataPertemuan.get("nama").asString
                        binding.txtPertemuanKe.text = dataPertemuan.get("ke").asString
                        binding.txtTanggal.text = dataPertemuan.get("tanggal").asString
                        binding.txtJam.text = "${dataPertemuan.get("jam_mulai").asString} - ${dataPertemuan.get("jam_selesai").asString}"
                        binding.txtDosen.text = dataPertemuan.get("dosen").asString
                        binding.txtStatus.text = dataPertemuan.get("status").asString
                        binding.cardView1.visibility = View.VISIBLE
                        if(data.has("status_absen")){
                            binding.txtStatusKehadiran.text = data.get("status_absen").asString
                            binding.txtWaktuPresensi.text = data.get("waktu_presensi").asString
                            binding.cardView2.visibility = View.VISIBLE
                        }else{
                            if (dataPertemuan.get("kode_status").asString == "1" && !data.has("status_absen") ){
                                binding.btnPresensi.visibility = View.VISIBLE
                            }
                        }
                    }
                } else {
                    // Handle the case where the data is not present
                }
            }
        })

        viewModel.absenResult.observe(viewLifecycleOwner, Observer { response ->
            nfcAdapter?.disableReaderMode(activity)
            if (response != null && response.isSuccessful) {
                val message = if (response.isSuccessful) {
                    response.body()?.get("message")?.asString ?: "Response Message Error"
                } else {
                    "Error Server"
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                // Lakukan refresh halaman Fragment jika permintaan absen berhasil
                if (response.isSuccessful) {
                    // Panggil kembali fungsi untuk mengambil data terbaru dari server
                    val pertemuanId = arguments?.getInt(ARG_PERTEMUAN_ID)
                    if (pertemuanId != null) {
                        val mahasiswaResponse = SharedData.mahasiswaResponse
                        if (mahasiswaResponse != null) {
                            val idMahasiswa = mahasiswaResponse.id
                            binding.btnPresensi.visibility = View.GONE
                            viewModel.getPertemuanDetail(pertemuanId,idMahasiswa)
                        }
                    }
                }
            }else{

            }
        })

        binding.btnPresensi.setOnClickListener {
            if (nfcAdapter == null) {
                showNfcNotSupportedDialog()
            } else if (!nfcAdapter!!.isEnabled) {
                showNfcNotEnabledDialog()
            } else {
                updateLocationAndShowNfcDialog()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(activity)
        stopLocationUpdates()
    }

    override fun onTagDiscovered(tag: Tag?) {
        activity?.runOnUiThread {
            val tagId = tag?.id
            val tagIdHex = tagId?.joinToString("") { byte -> "%02X".format(byte) }
            alertDialog.dismiss()
            val mahasiswaResponse = SharedData.mahasiswaResponse
            if (mahasiswaResponse != null) {
                val nimMahasiswa = mahasiswaResponse.nim
                val idMahasiswa = mahasiswaResponse.id

                val requestBody = JsonObject().apply {
                    // You can adjust the keys and values based on your API requirements
                    addProperty("nim", nimMahasiswa)
                    addProperty("nfcid", tagIdHex)
                    addProperty("imei", getIMEI(requireContext()) ?: "")
                    addProperty("long", longitude.toString())
                    addProperty("lat", latitude.toString())
                    addProperty("pertemuan_id", arguments?.getInt(ARG_PERTEMUAN_ID))
                    addProperty("mahasiswa_id", idMahasiswa)
                }
                viewModel.absen(requestBody)

            }
        }
    }

    private fun updateLocationAndShowNfcDialog() {
        if (isLocationPermissionGranted()) {
            // Start location updates
            startLocationUpdates()
            // Show NFC tap dialog after getting the location update
        } else {
            // Request location permissions
            requestLocationPermission()
        }
    }

    private fun showNfcTapDialog(selectedMahasiswa: String) {
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

    private fun startLocationUpdates() {
        // Check for location permission again
        if (isLocationPermissionGranted()) {
            // Request location updates
            try {
                locationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    0f,
                    this
                )
                // Tampilkan ProgressDialog
                showLocationProgressDialog()
                isLocationRequested = true
            } catch (e: SecurityException) {
                // Handle the case where the user has not granted location permission
                // You can show a message to the user or request permission again.
                requestLocationPermission()
            }
        } else {
            // Request location permission if not granted
            requestLocationPermission()
        }
    }


    private fun stopLocationUpdates() {
        locationManager?.removeUpdates(this)
        isLocationRequested = false
        progressDialog?.dismiss()
    }

    override fun onLocationChanged(location: Location) {
        // Here you can get the latitude and longitude from the 'location' object
        latitude = location.latitude
        longitude = location.longitude

        Log.e("asw-l",latitude.toString())
        Log.e("asw-ll",longitude.toString())


        // Use the latitude and longitude as needed
        // For example, you can update UI elements or send the data to the server

        // Remember to remove location updates when you're done using them
        stopLocationUpdates()

        // Now, show the NFC dialog
        showNfcTapDialog("usernameOnly")
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            // Show an explanation to the user why the location permission is needed (optional).
            // You can display a dialog or a Snackbar explaining the need for this permission.
            // Then request the permission again.
        } else {
            // Request the permission.
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun requestReadPhoneStatePermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.READ_PHONE_STATE),
                PERMISSION_REQUEST_READ_PHONE_STATE
            )
        } else {
            // Permission is already granted, proceed with your logic
            // You can call getIMEI() here or any other logic that requires the permission.
        }
    }

    private fun getIMEI(context: Context): String? {
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                telephonyManager.imei
            }else{
                @Suppress("DEPRECATION")
                telephonyManager.deviceId
            }
        }
        return null
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_READ_PHONE_STATE ->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with your logic
                    // You can call getIMEI() here or any other logic that requires the permission.
                } else {
                    // Permission denied, show a message or handle the situation accordingly
                }
            }
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, start NFC reading and location updates
                    updateLocationAndShowNfcDialog()
                } else {
                    // Permission denied, show a message or handle the situation accordingly
                }
            }
        }
    }

    // Fungsi untuk menampilkan ProgressDialog
    private fun showLocationProgressDialog() {
        progressDialog = ProgressDialog(requireContext())
        progressDialog?.setMessage("Mendapatkan lokasi...")
        progressDialog?.setCancelable(false)
        progressDialog?.show()

        // Mengatur batas waktu maksimum untuk mendapatkan lokasi (misalnya 10 detik)
        Handler().postDelayed({
            if (isLocationRequested) {
                // Sembunyikan ProgressDialog jika lokasi masih belum didapatkan setelah batas waktu tertentu
                progressDialog?.dismiss()
                isLocationRequested = false
                Toast.makeText(requireContext(), "Gagal mendapatkan lokasi", Toast.LENGTH_SHORT).show()
            }
        }, 10000) // 10000 milisecond = 10 detik
    }
}
