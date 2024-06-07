package com.demo.nfcsample

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.demo.nfcsample.databinding.ActivityMainBinding
import com.demo.nfcsample.nfc.NfcBaseActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //private var nfcIntentFilters: Array<IntentFilter>? = null

    private var nfcThread: HandlerThread? = null
    private var nfcThreadHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

//        nfcThread = HandlerThread("nfc thread")
//        nfcThread!!.start()
//        nfcThreadHandler = Handler(nfcThread!!.looper)

        binding.fab.setOnClickListener {
            startActivity(Intent(this, NfcBaseActivity::class.java))
        }


    }



//    override fun onResume() {
//        super.onResume()
//        // Get all NDEF discovered intents
//        // Makes sure the app gets all discovered NDEF messages as long as it's in the foreground.
////        nfcAdapter?.enableForegroundDispatch(this, nfcPendingIntent, null, null);
//        nfcAdapter?.enableForegroundDispatch(this, nfcPendingIntent, arrayOf(ndefIntentFilter, techIntentFilter, tagIntentFilter), null)
////        val isNfcEnabled = "NFC enabled: ${(nfcAdapter?.isEnabled).toString()}"
////        val isNfcSuported = "NFC Supported: ${(nfcAdapter != null).toString()}"
////
////        binding.tvNFCEnabled.text = isNfcEnabled
////        binding.tvNFCSupported.text= isNfcSuported
//
//        // Alternative: only get specific HTTP NDEF intent
//        //nfcAdapter?.enableForegroundDispatch(this, nfcPendingIntent, nfcIntentFilters, null);
//    }


//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        val tag: Tag? = intent!!.getParcelableExtra(NfcAdapter.EXTRA_TAG)
//
//        if (tag != null) {
//            Log.d(TAG, "onNewIntent: tag: $tag")
//        }else{
//            Log.d(TAG, "onNewIntent: tag nullo")
//        }
//
//        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
//            val rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
//            val messages = rawMessages?.map { it as NdefMessage }
//
//        }
//
//
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        if (nfcThread != null) {
//            nfcThread!!.quitSafely()
//            nfcThread = null
//            nfcThreadHandler = null
//        }
//    }

    companion object {
        private const val TAG = "MainActivity"
    }

//    override fun onTagDiscovered(p0: Tag?) {
//        Log.d(TAG, "onTagDiscovered: tag $p0")
//
//        val tag: Tag? = intent!!.getParcelableExtra(NfcAdapter.EXTRA_TAG)
//
//        if (tag != null) {
//            Log.d(TAG, "onTagDiscovered: tag: $tag")
//        }else{
//            Log.d(TAG, "onTagDiscovered: tag nullo")
//        }
//
//    }
}