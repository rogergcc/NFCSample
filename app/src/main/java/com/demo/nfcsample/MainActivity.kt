package com.demo.nfcsample

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.NfcA
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.demo.nfcsample.databinding.ActivityMainBinding
import com.demo.nfcsample.nfc.NfcBaseActivity


class MainActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private lateinit var binding: ActivityMainBinding

    private var nfcAdapter: NfcAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding.fab.setOnClickListener {
            startActivity(Intent(this, NfcBaseActivity::class.java))
        }

        try {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this)
            if (nfcAdapter == null) {
                Log.d("MainActivity", "NFC no es compatible con este dispositivo")
                Toast.makeText(this, "NFC No es compatible", Toast.LENGTH_SHORT).show()
                return
            }
            if (!nfcAdapter!!.isEnabled) {
                Log.d("MainActivity", "NFC está deshabilitado")
                Toast.makeText(this, "NFC desahabilitado", Toast.LENGTH_SHORT).show()
                return
            }
        } catch (e: Exception) {
            Log.e(TAG, "onCreate: error : ${e.message}")
        }

    }

    override fun onTagDiscovered(tag: Tag?) {
        Log.d(TAG, "Discovered!  ")
        if (tag != null) {
            val id = tag.id
            Log.d(TAG, "onTagDiscovered: tag id raw: ${tag.id}")
            Log.d(TAG, "ID de la tarjeta: ${id.joinToString("") { "%02x".format(it) }}")

            // Obtain the list of technologies supported by this tag
            val techList = tag.techList
            Log.d(TAG, "Tecnologías soportadas: ${techList.joinToString(", ")}")


            // In case the tag supports NfcA, you can get more information about the tag
            if (NfcA::class.java.name in techList) {
                val nfcA = NfcA.get(tag)
                Log.d(TAG, "[nfcA] Sak: ${nfcA.sak}")
                Log.d(TAG, "[nfcA] Atqa: ${nfcA.atqa.joinToString("") { "%02x".format(it) }}")
                Log.d(TAG, "[nfcA] Max Transceive Length: ${nfcA.maxTransceiveLength}")
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

        } else {
            Log.d(TAG, "No se descubrió ninguna tarjeta")
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Reader Mode")

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
        Log.d(TAG, "tag detected")
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
    }


    companion object {
        private const val TAG = "MainActivity"
    }

}