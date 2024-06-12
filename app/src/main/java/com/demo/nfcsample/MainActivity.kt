package com.demo.nfcsample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.Ndef
import android.nfc.tech.NfcA
import android.nfc.tech.NfcB
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.demo.nfcsample.databinding.ActivityMainBinding
import com.demo.nfcsample.nfc.NfcBaseActivity
import java.util.*


class MainActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private lateinit var binding: ActivityMainBinding

    private var nfcAdapter: NfcAdapter? = null

    private lateinit var viewModel: NfcViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding.fab.setOnClickListener {
            startActivity(Intent(this, NfcBaseActivity::class.java))
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
            binding.tvNFCContent.text = getText(R.string.no_tag_detected)
            return
        }

        handleTag(tag)
        val id = tag.id
        Log.d(TAG, "onTagDiscovered: tag id raw: ${tag.id}")
        Log.d(TAG, "ID de la tarjeta: ${id.joinToString("") { "%02x".format(it) }}")

        // Obtain the list of technologies supported by this tag
        val techList = tag.techList
        Log.d(TAG, "Tecnologías soportadas: ${techList.joinToString(", ")}")

        // In case the tag supports Ndef, you can get the Ndef message
        if (Ndef::class.java.name in tag.techList) {
            val ndef = Ndef.get(tag)
            val ndefMessage = ndef.cachedNdefMessage
            val records = ndefMessage.records
            for (record in records) {
                if (record.tnf == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(
                        record.type,
                        NdefRecord.RTD_URI
                    )
                ) {
                    val uri = record.toUri()
                    Log.d(TAG, "URL found: $uri")
                }
            }
        }
        // In case the tag supports NfcA, you can get more information about the tag
        if (NfcA::class.java.name in techList) {
            val nfcA = NfcA.get(tag)
            Log.d(TAG, "[nfcA] Sak: ${nfcA.sak}")
            Log.d(TAG, "[nfcA] Atqa: ${nfcA.atqa.joinToString("") { "%02x".format(it) }}")
            Log.d(TAG, "[nfcA] Max Transceive Length: ${nfcA.maxTransceiveLength}")
        }
        if (NfcB::class.java.name in techList) {
            val nfcB = NfcB.get(tag)
            Log.d(
                TAG,
                "[nfcB] App Data: ${nfcB.applicationData.joinToString("") { "%02x".format(it) }}"
            )
            Log.d(
                TAG,
                "[nfcB] Prot Info: ${nfcB.protocolInfo.joinToString("") { "%02x".format(it) }}"
            )
            Log.d(TAG, "[nfcB] Max Transceive Length: ${nfcB.maxTransceiveLength}")
        }

        // In case the tag supports IsoDep, you can get more information about the tag
        if (IsoDep::class.java.name in techList) {
            val isoDep = IsoDep.get(tag)
            Log.d(TAG, "[isoDep] Historical bytes: ${
                isoDep.historicalBytes?.joinToString("") {
                    "%02x".format(it)
                }
            }")
            Log.d(TAG, "[isoDep] Hi layer response: ${
                isoDep.hiLayerResponse?.joinToString("") {
                    "%02x".format(it)
                }
            }")
            Log.d(TAG, "[isoDep] Timeout: ${isoDep.timeout}")
            Log.d(TAG, "[isoDep] Max Transceive Length: ${isoDep.maxTransceiveLength}")


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
        val nfcA = NfcA.get(tag)
        val isoDep = IsoDep.get(tag)
        val tagInfo = StringBuilder().apply {
            append("Technologies: ")
            if (nfcA != null) append("NfcA ")
            if (isoDep != null) append("IsoDep ")
            append("\n")

            append("Technologies 2: ")
            append(tag.techList.joinToString("\n") { it })
            append("\n")

            append("Tag Id Raw: ${tag.id}\n")
            append("Tag ID: ${tag.id.joinToString(":") { "%02x".format(it) }}\n")
            append("\n")
            append("Serial Number: ${tag.id.joinToString(":") { "%02x".format(it) }}\n")
            if (nfcA != null) {
                append("ATQA: ${nfcA.atqa.joinToString("") { "%02x".format(it) }}\n")
                append("SAK: ${"%02x".format(nfcA.sak)}\n")
            }
            if (isoDep != null) {
                append("ATS: ${isoDep.historicalBytes?.joinToString("") { "%02x".format(it) }}\n")
            }
        }.toString()
//        binding.tvNFCContent.text = tagInfo
        runOnUiThread { binding.tvNFCContent.text = tagInfo }
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