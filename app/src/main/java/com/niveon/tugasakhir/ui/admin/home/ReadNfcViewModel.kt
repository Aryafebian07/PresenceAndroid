package com.niveon.tugasakhir.ui.admin.home

import android.app.Application
import android.content.ContentValues
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.niveon.tugasakhir.util.NFCManager
import com.niveon.tugasakhir.util.NFCStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.experimental.and

class ReadNfcViewModel(application: Application) : AndroidViewModel(application),NfcAdapter.ReaderCallback {

    companion object {
        private val TAG = ReadNfcViewModel::class.java.getSimpleName()
        private const val prefix = "android.nfc.tech."
    }

    private val liveNFC: MutableStateFlow<NFCStatus?>
    private val liveToast: MutableSharedFlow<String?>
    private val liveTag: MutableStateFlow<String?>

    init {
        liveNFC = MutableStateFlow(null)
        liveToast = MutableSharedFlow()
        liveTag = MutableStateFlow(null)
    }

    //region Toast Methods
    private fun updateToast(message: String) {
        viewModelScope.launch {
            liveToast.emit(message)
        }
    }

    fun observeToast(): SharedFlow<String?> {
        return liveToast.asSharedFlow()
    }
    //endregion

    fun getNFCFlags(): Int {
        return NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_NFC_B or
                NfcAdapter.FLAG_READER_NFC_F or
                NfcAdapter.FLAG_READER_NFC_V or
                NfcAdapter.FLAG_READER_NFC_BARCODE
    }

    fun getExtras(): Bundle {
        val options: Bundle = Bundle()
        options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 30000)
        return options
    }

    //region NFC Methods
    fun onCheckNFC(isChecked: Boolean) {
        if (isChecked) {
            postNFCStatus(NFCStatus.Tap)
        } else {
            postNFCStatus(NFCStatus.NoOperation)
            updateToast("NFC is Disabled, Please Toggle On!")
        }
    }

    fun readTag(tag: Tag?) {
        postNFCStatus(NFCStatus.Process)
        val stringBuilder: StringBuilder = StringBuilder()
        val id: ByteArray? = tag?.id
        stringBuilder.append("Tag ID (hex): ${getHex(id!!)} \n")
        stringBuilder.append("Tag ID (dec): ${getDec(id)} \n")
        stringBuilder.append("Tag ID (reversed): ${getReversed(id)} \n")
        stringBuilder.append("Technologies: ")
        tag.techList.forEach { tech ->
            stringBuilder.append(tech.substring(prefix.length))
            stringBuilder.append(", ")
        }
        stringBuilder.delete(stringBuilder.length - 2, stringBuilder.length)
        tag.techList.forEach { tech ->
            if (tech.equals(MifareClassic::class.java.name)) {
                stringBuilder.append('\n')
                val mifareTag: MifareClassic = MifareClassic.get(tag)
                val type: String = when (mifareTag.type) {
                    MifareClassic.TYPE_CLASSIC -> "Classic"
                    MifareClassic.TYPE_PLUS -> "Plus"
                    MifareClassic.TYPE_PRO -> "Pro"
                    else -> "Unknown"
                }
                stringBuilder.append("Mifare Classic type: $type \n")
                stringBuilder.append("Mifare size: ${mifareTag.size} bytes \n")
                stringBuilder.append("Mifare sectors: ${mifareTag.sectorCount} \n")
                stringBuilder.append("Mifare blocks: ${mifareTag.blockCount}")
            }
            if (tech.equals(MifareUltralight::class.java.name)) {
                stringBuilder.append('\n')
                val mifareUlTag: MifareUltralight = MifareUltralight.get(tag)
                val type: String = when (mifareUlTag.type) {
                    MifareUltralight.TYPE_ULTRALIGHT -> "Ultralight"
                    MifareUltralight.TYPE_ULTRALIGHT_C -> "Ultralight C"
                    else -> "Unknown"
                }
                stringBuilder.append("Mifare Ultralight type: $type")
            }
            if (tech == Ndef::class.java.name) {
                val ndefTag = Ndef.get(tag)
                ndefTag?.connect()
                Log.d(TAG, "NDEF Connected: ${ndefTag.isConnected}")

                ndefTag?.ndefMessage?.let { ndefMessage ->
                    val records = ndefMessage.records
                    for (record in records) {
                        val payload = record.payload
                        if (payload.isNotEmpty()) {
                            val textEncoding = if ((payload[0].toInt() and 128) == 0) "UTF-8" else "UTF-16"
                            val languageCodeLength = payload[0].toInt() and 63
                            if (languageCodeLength < payload.size - 1) {
                                val textData = payload.copyOfRange(languageCodeLength + 1, payload.size)
                                val text = String(textData, charset(textEncoding))
                                Log.d(TAG, "NDEF Record Data: $text")
                            } else {
                                Log.d(TAG, "Invalid NDEF Record Payload")
                            }
                        } else {
                            Log.d(TAG, "Empty NDEF Record Payload")
                        }
                    }
                }
                ndefTag?.close()
            }
        }
        Log.d(TAG, "Datum: $stringBuilder")
        Log.d(ContentValues.TAG, "dumpTagData Return \n $stringBuilder")
        postNFCStatus(NFCStatus.Read)
        liveTag.value = "${getDateTimeNow()} \n ${stringBuilder}"
    }

    private fun postNFCStatus(status: NFCStatus) {
        if (NFCManager.isSupportedAndEnabled(getApplication())) {
            liveNFC.value = status
        } else if (NFCManager.isNotEnabled(getApplication())) {
            liveNFC.value = NFCStatus.NotEnabled
            updateToast("Please Enable your NFC!")
            liveTag.value = "Please Enable your NFC!"
        } else if (NFCManager.isNotSupported(getApplication())) {
            liveNFC.value = NFCStatus.NotSupported
            updateToast("NFC Not Supported!")
            liveTag.value = "NFC Not Supported!"
        }
        if (NFCManager.isSupportedAndEnabled(getApplication()) && status == NFCStatus.Tap) {
            liveTag.value = "Please Tap Now!"
        } else {
            liveTag.value = null
        }
    }

    fun observeNFCStatus(): StateFlow<NFCStatus?> {
        return liveNFC.asStateFlow()
    }
    //endregion

    //region Tags Information Methods
    private fun getDateTimeNow(): String {
        val TIME_FORMAT: DateFormat = SimpleDateFormat.getDateTimeInstance()
        val now: Date = Date()
        return TIME_FORMAT.format(now)
    }

    private fun getHex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (i in bytes.indices.reversed()) {
            val b: Int = bytes[i].and(0xff.toByte()).toInt()
            if (b < 0x10) sb.append('0')
            sb.append(Integer.toHexString(b))
            if (i > 0)
                sb.append(" ")
        }
        return sb.toString()
    }

    private fun getDec(bytes: ByteArray): Long {
        var result: Long = 0
        var factor: Long = 1
        for (i in bytes.indices) {
            val value: Long = bytes[i].and(0xffL.toByte()).toLong()
            result += value * factor
            factor *= 256L
        }
        return result
    }

    private fun getReversed(bytes: ByteArray): Long {
        var result: Long = 0
        var factor: Long = 1
        for (i in bytes.indices.reversed()) {
            val value = bytes[i].and(0xffL.toByte()).toLong()
            result += value * factor
            factor *= 256L
        }
        return result
    }

    fun observeTag(): StateFlow<String?> {
        return liveTag.asStateFlow()
    }
    //endregion
    override fun onTagDiscovered(tag: Tag?) {
        // Implementasikan logika untuk membaca tag NFC di sini
        // Misalnya, baca data dari tag NFC dan gunakan fungsi readTag
        readTag(tag)
    }
}
