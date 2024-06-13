package com.demo.nfcsample

import android.media.RingtoneManager
import android.nfc.*
import android.nfc.tech.Ndef
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.demo.nfcsample.databinding.ActivityWriteNfcBinding
import java.io.IOException

class WriteNfcActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {
    private lateinit var binding: ActivityWriteNfcBinding
    private var mNfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityWriteNfcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
    }

    override fun onResume() {
        super.onResume()
        enableReaderMode()

    }
    private fun enableReaderMode() {
        if (mNfcAdapter != null) {
            val options = Bundle()
            options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250)

            mNfcAdapter!!.enableReaderMode(
                this,
                this,
                NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_NFC_B or
                        NfcAdapter.FLAG_READER_NFC_F or
                        NfcAdapter.FLAG_READER_NFC_V or
                        NfcAdapter.FLAG_READER_NFC_BARCODE,
                options
            )
        }
    }
    override fun onPause() {
        super.onPause()
        if (mNfcAdapter != null) mNfcAdapter!!.disableReaderMode(this)
    }

    override fun onTagDiscovered(tag: Tag?) {
        val mNdef = Ndef.get(tag)
        if (mNdef != null) {
            val mNdefMessage = mNdef.cachedNdefMessage
            val mRecord = NdefRecord.createTextRecord("en", binding.edNFC.text.toString())
            val mMsg = NdefMessage(mRecord)

            try {
                mNdef.connect()
                mNdef.writeNdefMessage(mMsg)

                runOnUiThread {
                    binding.tvWriteResult.text = "Write to NFC Success $mMsg"
                }

            } catch (e: Exception) {
                // if the NDEF Message to write is malformed
            } finally {
                try {
                    mNdef.close()
                } catch (e: IOException) {
                    // if the operation is interrupted
                }
            }
        }
    }
}