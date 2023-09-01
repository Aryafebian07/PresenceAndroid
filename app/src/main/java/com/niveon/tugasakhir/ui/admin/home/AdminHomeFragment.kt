import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.niveon.tugasakhir.R
import com.niveon.tugasakhir.databinding.FragmentAdminHomeBinding
import com.niveon.tugasakhir.ui.admin.home.AdminHomeViewModel
import com.niveon.tugasakhir.ui.admin.home.WriteNfcAdminFragment
import com.niveon.tugasakhir.ui.admin.home.WriteNfcDosenFragment
import com.niveon.tugasakhir.ui.admin.home.WriteNfcMahasiswaFragment
import com.niveon.tugasakhir.util.Constants
import com.niveon.tugasakhir.util.SharedData

class AdminHomeFragment : Fragment() {
    private lateinit var binding: FragmentAdminHomeBinding
    private lateinit var viewModel: AdminHomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(AdminHomeViewModel::class.java)
        animasi_layout()
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonReadNfc.setOnClickListener {
            // Handle read NFC button click
            Toast.makeText(requireContext(), "Read NFC Button Clicked", Toast.LENGTH_SHORT).show()
            val ReadNfcFragment = ReadNfcFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ReadNfcFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.buttonWriteNfcAdmin.setOnClickListener {
            // Handle write NFC Admin button click
            Toast.makeText(requireContext(), "Write NFC Admin Button Clicked", Toast.LENGTH_SHORT).show()
            // Navigate to WriteNfcAdminFragment
            val writeNfcAdminFragment = WriteNfcAdminFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, writeNfcAdminFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.buttonWriteNfcDosen.setOnClickListener {
            // Handle write NFC Dosen button click
            Toast.makeText(requireContext(), "Write NFC Dosen Button Clicked", Toast.LENGTH_SHORT).show()
            // Navigate to WriteNfcDosenFragment
            val writeNfcDosenFragment = WriteNfcDosenFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, writeNfcDosenFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.buttonWriteNfcMahasiswa.setOnClickListener {
            // Handle write NFC Mahasiswa button click
            Toast.makeText(requireContext(), "Write NFC Mahasiswa Button Clicked", Toast.LENGTH_SHORT).show()
            // Navigate to WriteNfcMahasiswaFragment
            val writeNfcMahasiswaFragment = WriteNfcMahasiswaFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, writeNfcMahasiswaFragment)
                .addToBackStack(null)
                .commit()
        }

        val adminResponse = SharedData.adminResponse
        if (adminResponse != null) {
            val nama = adminResponse.nama
            binding.txtNamaMahasiswa.text = nama

            if (nama!!.length > 15) {
                // Hitung lebar teks txtNamaMahasiswa
                val txtNamaMahasiswaWidth = binding.txtNamaMahasiswa.paint.measureText(nama)

                // Set margin kiri untuk imgProfile berdasarkan lebar teks txtNamaMahasiswa
                val layoutParams = binding.imgProfile.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.marginStart = txtNamaMahasiswaWidth.toInt()
                binding.imgProfile.layoutParams = layoutParams
            }

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
            binding.hello.animation = fade_in
            binding.txtNamaMahasiswa.animation = fade_in
            binding.imgProfile.animation = fade_in
            binding.menuList.animation = fade_in
        }
        handler.postDelayed(runnable, 1000)
    }
}
