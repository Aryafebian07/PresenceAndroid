package com.niveon.tugasakhir.ui.admin.home

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niveon.tugasakhir.util.Constants.encryptionKey
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESedeKeySpec
import javax.crypto.spec.IvParameterSpec

class WriteNfcAdminViewModel : ViewModel() {

    private val _writeResult: MutableLiveData<Boolean?> = MutableLiveData()
    val writeResult: MutableLiveData<Boolean?>
        get() = _writeResult

    fun resetWriteResult() {
        _writeResult.value = null
    }

    fun writeTag(tag: Tag?, messageToWrite: String) {
        tag?.let {
            val ndefTag = Ndef.get(it)
            ndefTag?.connect()

            if (ndefTag == null || !ndefTag.isWritable) {
                _writeResult.value = false
                return
            }
            val messageBytes = encryptWith3DES(messageToWrite)
            val mimeBytes = "dosen/id".toByteArray(Charsets.US_ASCII)
//            val messageBytes = messageToWrite.toByteArray(Charsets.UTF_8)
            val payload = ByteArray(1 + mimeBytes.size + messageBytes.size)
            payload[0] = 0x02 // Status byte: NFC Forum Well-Known Type
            System.arraycopy(mimeBytes, 0, payload, 1, mimeBytes.size)
            System.arraycopy(messageBytes, 0, payload, 1 + mimeBytes.size, messageBytes.size)

            val record = NdefRecord(NdefRecord.TNF_WELL_KNOWN,mimeBytes, ByteArray(0), payload)
            val message = NdefMessage(arrayOf(record))

            val size = message.toByteArray().size
            if (ndefTag.maxSize < size) {
                _writeResult.value = false
                return
            }

            ndefTag.writeNdefMessage(message)
            _writeResult.value = true

            ndefTag.close()
        }
    }
    // Fungsi untuk melakukan encoding 3DES pada data
    private fun encryptWith3DES(data: String): ByteArray {

        // Ubah kunci menjadi byte array
        val keyBytes = encryptionKey.toByteArray(Charsets.UTF_8)

        // Buat DESedeKeySpec dari kunci
        val keySpec: KeySpec = DESedeKeySpec(keyBytes)
        val keyFactory = SecretKeyFactory.getInstance("DESede")
        val secretKey: SecretKey = keyFactory.generateSecret(keySpec)

        // Buat objek Cipher dengan mode enkripsi 3DES (ECB)
        val cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding")

        // Inisialisasi Cipher dengan kunci
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        // Enkripsi data dan kembalikan hasil enkripsi sebagai byte array
        return cipher.doFinal(data.toByteArray(Charsets.UTF_8))
    }
}
