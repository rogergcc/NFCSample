package com.demo.nfcsample.nfc

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

 open class NfcBaseActivity : AppCompatActivity() {

     private var nfcAdapter: NfcAdapter? = null
     private var nfcPendingIntent: PendingIntent? = null
     private lateinit var intentFiltersArray: Array<IntentFilter>

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)

         // Check if NFC is supported and enabled
         nfcAdapter = NfcAdapter.getDefaultAdapter(this)
         if (nfcAdapter == null) {
             Toast.makeText(this, "NFC not supported", Toast.LENGTH_LONG).show()
             finish()
             return
         }

         // Check if NFC is enabled
         if (!nfcAdapter!!.isEnabled) {
             Toast.makeText(this, "NFC is disabled", Toast.LENGTH_LONG).show()
         }

         // Setup PendingIntent for foreground dispatch
         val nfcIntent =  Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)


         // Since Android 12 (API level 31) it's mandatory to specify mutability
         // of PendingIntent. We need a mutable intent, which was a default
         // option earlier.
         // Since Android 12 (API level 31) it's mandatory to specify mutability
         // of PendingIntent. We need a mutable intent, which was a default
         // option earlier.
         val flags =
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0

         nfcPendingIntent = PendingIntent.getActivity(
             this,
             0,
             nfcIntent,
             flags
         )

         // Setup IntentFilter for NDEF discovered
         val ndefIntentFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply {
             try {
                 addDataType("*/*")
             } catch (e: IntentFilter.MalformedMimeTypeException) {
                 throw RuntimeException("Malformed MIME type", e)
             }
         }
         intentFiltersArray = arrayOf(ndefIntentFilter)
     }
     fun getFlagCompat() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
          PendingIntent.FLAG_MUTABLE
     } else {
         PendingIntent.FLAG_UPDATE_CURRENT
     }
     override fun onResume() {
         super.onResume()
         Log.d(TAG, "onResume: Enabling foreground dispatch")
         nfcAdapter?.enableForegroundDispatch(this, nfcPendingIntent, intentFiltersArray, null)
     }

     override fun onPause() {
         super.onPause()
         Log.d(TAG, "onPause: Disabling foreground dispatch")
         nfcAdapter?.disableForegroundDispatch(this)
     }

     override fun onNewIntent(intent: Intent?) {
         super.onNewIntent(intent)
         Toast.makeText(this, "Intent", Toast.LENGTH_SHORT).show()
         Log.d(TAG, "onNewIntent: Received new intent")

         if (intent == null) return

         if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action ||
             NfcAdapter.ACTION_TAG_DISCOVERED == intent.action ||
             NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {

             val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
             if (tag != null) {
                 handleNfcTag(tag)
             }
         }
     }

     private fun handleNfcTag(tag: Tag) {
         Log.d(TAG, "handleNfcTag: Tag detected")
         // Process the NFC tag
         val ndef = Ndef.get(tag)
         if (ndef != null) {
             // Do something with the NDEF tag
             Log.d(TAG, "handleNfcTag: NDEF tag found")
         } else {
             // Handle non-NDEF tags if necessary
             Log.d(TAG, "handleNfcTag: Non-NDEF tag found")
         }
     }

     companion object {
         private const val TAG = "NfcBaseActivity"
     }
}