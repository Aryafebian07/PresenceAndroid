import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.niveon.tugasakhir.databinding.FragmentReadNfcBinding
import com.niveon.tugasakhir.ui.admin.home.ReadNfcViewModel
import com.niveon.tugasakhir.util.NFCManager
import com.niveon.tugasakhir.util.NFCStatus
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ReadNfcFragment : Fragment(), CompoundButton.OnCheckedChangeListener {

    companion object {
        private val TAG = ReadNfcFragment::class.java.getSimpleName()
    }

    private var _binding: FragmentReadNfcBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReadNfcViewModel by lazy {
        ViewModelProvider(this).get(ReadNfcViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReadNfcBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toggleButton.setOnCheckedChangeListener(this)

        observeNfcStatus()
        observeToast()
        observeTag()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeToast() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.observeToast().collectLatest { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun observeTag() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.observeTag().collectLatest { tag ->
                binding.textViewExplanation.text = tag

                binding.textViewTagRecord.text = extractTagRecordData(tag)
            }
        }
    }

    private fun extractTagRecordData(tagData: String?): String? {
        val startIndex = tagData?.indexOf("NDEF Data:") ?: return ""
        if (startIndex >= 0 && startIndex < tagData.length) {
            return tagData.substring(startIndex)
        }
        return ""
    }

    private fun observeNfcStatus(){
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.observeNFCStatus().collectLatest { status ->
                Log.d(TAG, "observeNFCStatus $status")
                when (status) {
                    NFCStatus.NoOperation -> NFCManager.disableReaderMode(requireContext(), requireActivity())
                    NFCStatus.Tap -> NFCManager.enableReaderMode(requireContext(), requireActivity(), viewModel, viewModel.getNFCFlags(), viewModel.getExtras())
                    else -> {
                        // Do nothing or handle other NFC statuses if needed
                    }
                }
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (buttonView == binding.toggleButton) {
            viewModel.onCheckNFC(isChecked)
        }
    }
}
