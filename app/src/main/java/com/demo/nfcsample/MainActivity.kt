package com.demo.nfcsample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.Ndef
import android.nfc.tech.NfcA
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.demo.nfcsample.databinding.ActivityMainBinding
import java.nio.charset.Charset


class MainActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private lateinit var binding: ActivityMainBinding

    private var nfcAdapter: NfcAdapter? = null

    private lateinit var viewModel: NfcViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding.writeNfcButton.setOnClickListener {
            startActivity(Intent(this, WriteNfcActivity::class.java))
        }

        viewModel = ViewModelProvider(this)[NfcViewModel::class.java]
        viewModel.checkNfcStatus()
        setUpObservers()
        nfcAdapter = viewModel.getNfcAdapter()

    }

    private fun setUpObservers() {
        viewModel.nfcStatus.observe(this) { status ->
            when (status) {
                NfcStatus.ENABLED -> {
                    binding.nfcStatus.text = getString(R.string.nfc_enabled)
                    binding.nfcIcon.setImageResource(R.drawable.nfc_enabled)
                }
                NfcStatus.DISABLED -> {
                    binding.nfcStatus.text = getString(R.string.nfc_disabled)
                    binding.nfcIcon.setImageResource(R.drawable.nfc_disabled)
                    //openNfcSettings()
                }
                NfcStatus.NOT_SUPPORTED -> {
                    binding.nfcStatus.text = getString(R.string.nfc_not_supported)
                    binding.nfcIcon.setImageResource(R.drawable.nfc_disabled)
                }
            }
        }
    }

    private fun openNfcSettings() {
        val intent = Intent(Settings.ACTION_NFC_SETTINGS)
        startActivity(intent)
    }

    override fun onTagDiscovered(tag: Tag?) {
        Log.d(TAG, "Discovered!  ")
        if (tag == null) {
            Log.d(TAG, "No se descubrió ninguna tarjeta")
            binding.tvNFCTechsDetails.text = getText(R.string.no_tag_detected)
            return
        }

        handleTag(tag)



        // In case the tag supports Ndef, you can get the Ndef message
        if (Ndef::class.java.name in tag.techList) {
            val ndef = Ndef.get(tag)
            val ndefMessage = ndef?.cachedNdefMessage
            val records = ndefMessage?.records
            runOnUiThread {
                if (records != null) {
                    for (record in records) {
                        val payload = record.payload

                        val textEncoding = if (payload[0].toInt() and 128 == 0) "UTF-8" else "UTF-16"
                        val languageCodeLength = payload[0].toInt() and 63
                        val text = String(payload, languageCodeLength + 1, payload.size - languageCodeLength - 1,
                            Charset.forName(textEncoding))

                        binding.tvNFCContent.text = text
                    }
                } else {
                    binding.tvNFCContent.text = "No NDEF records found."
                }
            }

//            for (record in records) {
//
//                if (record.tnf == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(
//                        record.type,
//                        NdefRecord.RTD_URI
//                    )
//                ) {
//                    val uri = record.toUri()
//                    Log.d(TAG, "URL found: $uri")
//                }
//            }
        }

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Reader Mode")
        registerReceiver(nfcStateReceiver, IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED))
        //

        // Work around for some broken Nfc firmware implementations that poll the card too fast
        val options = Bundle()
        options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 2500)

        nfcAdapter!!.enableReaderMode(
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

    private fun handleTag(tag: Tag) {
        val tagInfo = StringBuilder().apply {
            append("Technologies : \n")
            append(tag.techList.joinToString("\n") { "+ $it" })
            append("\n")
            append("\n")
            append("Tag Id Raw: ${tag.id}\n")
            append("Tag ID: ${tag.id.joinToString(":") { "%02x".format(it) }}\n")

            append("Tag Type: ${tag.describeContents()}\n")

            if (Ndef::class.java.name in tag.techList) {
                val ndef = Ndef.get(tag)
                val ndefMessage = ndef.cachedNdefMessage
                val records = ndefMessage?.records
                append("\n")
                append("NDEF Message: \n")
                append("NDEF type ${ndef.type} \n")
                append("NDEF maxSize ${ndef.maxSize} \n")
                append("NDEF message size ${ndefMessage?.toByteArray()?.size} \n")
                append("Records: \n")
                records?.forEachIndexed { index, record ->

                    append("Record $index: \n")
                    append("Id: ${record.id}\n")

                    append("TNF: ${record.tnf}\n")
                    append("Type: ${record.type}\n")
                    append("Payload: ${record.payload}\n")
                    val payload = record.payload
                    val text = String(payload)
                    append("Text: $text\n")
                    append("\n")
                }
            }
            if (IsoDep::class.java.name in tag.techList) {
                val isoDep = IsoDep.get(tag)
                append("IsoDep: \n")
                append("IsoDep Timeout: ${isoDep?.timeout}\n")
                append("IsoDep MaxTransceiveLength: ${isoDep?.maxTransceiveLength}\n")
            }
            if (NfcA::class.java.name in tag.techList) {
                val nfcA = NfcA.get(tag)
                append("NfcA: \n")
                append("NfcA ATQA: ${nfcA?.atqa?.joinToString("") { "%02x".format(it) }}\n")
                append("NfcA SAK: ${"%02x".format(nfcA?.sak)}\n")
                append("NfcA MaxTransceiveLength: ${nfcA?.maxTransceiveLength}\n")
                append("NfcA Timeout: ${nfcA?.timeout}\n")
            }



        }.toString()
//        binding.tvNFCContent.text = tagInfo
        runOnUiThread { binding.tvNFCTechsDetails.text = tagInfo }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(nfcStateReceiver)
        nfcAdapter?.disableReaderMode(this)
    }

    private val nfcStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (NfcAdapter.ACTION_ADAPTER_STATE_CHANGED == intent?.action) {
                viewModel.checkNfcStatus()
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}