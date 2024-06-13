package com.demo.nfcsample

import android.media.RingtoneManager
import android.nfc.*
import android.nfc.tech.Ndef
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.demo.nfcsample.databinding.ActivityMainBinding
import com.demo.nfcsample.databinding.ActivityWriteNfcBinding
import java.io.IOException

class WriteNfcActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {
    private lateinit var progressDialog: AlertDialog
    private lateinit var binding: ActivityWriteNfcBinding
    private var mNfcAdapter: NfcAdapter? = null
    private var isButtonPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityWriteNfcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        progressDialog = AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.waiting_tap_nfc))
            setCancelable(false)
            setView(ProgressBar(this@WriteNfcActivity))
        }.create()

        binding.btnWrite.setOnClickListener {
            progressDialog.show()
            isButtonPressed = true
            enableReaderMode()
        }
    }

    override fun onResume() {
        super.onResume()
        enableReaderMode()

    }
    private fun enableReaderMode() {
        if (mNfcAdapter != null && isButtonPressed) {
            val options = Bundle()
            options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250)

            mNfcAdapter!!.enableReaderMode(
                this,
                this,
                NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_B or NfcAdapter.FLAG_READER_NFC_F or NfcAdapter.FLAG_READER_NFC_V or NfcAdapter.FLAG_READER_NFC_BARCODE or NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                options
            )
        }
    }
    override fun onPause() {
        super.onPause()
        if (mNfcAdapter != null) mNfcAdapter!!.disableReaderMode(this)
    }

    override fun onTagDiscovered(tag: Tag?) {

        isButtonPressed = false
        val mNdef = Ndef.get(tag)
        if (mNdef != null) {
            val mNdefMessage = mNdef.cachedNdefMessage
            val mRecord = NdefRecord.createTextRecord("en", binding.edNFC.text.toString())
            val mMsg = NdefMessage(mRecord)

            try {
                mNdef.connect()
                mNdef.writeNdefMessage(mMsg)

                runOnUiThread {
                    Toast.makeText(
                        applicationContext, "Write to NFC Success $mMsg", Toast.LENGTH_SHORT
                    ).show()
                    progressDialog.dismiss()
                }

                try {
                    val notification =
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val r = RingtoneManager.getRingtone(
                        applicationContext, notification
                    )
                    r.play()
                } catch (e: Exception) {
                    // Some error playing sound
                }
            } catch (e: FormatException) {
                // if the NDEF Message to write is malformed
            } catch (e: TagLostException) {
                // Tag went out of range before operations were complete
            } catch (e: IOException) {
                // if there is an I/O failure, or the operation is cancelled
            } catch (e1: SecurityException) {
                try {
                    mNdef.close()
                } catch (e: IOException) {
                    // if there is an I/O failure, or the operation is cancelled
                }
            }
        }
    }
}