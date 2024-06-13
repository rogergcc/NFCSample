package com.demo.nfcsample.nfc

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
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

abstract class NfcBaseActivity : AppCompatActivity() ,NfcAdapter.ReaderCallback {

     protected var nfcAdapter: NfcAdapter? = null

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         nfcAdapter = NfcAdapter.getDefaultAdapter(this)
     }

     override fun onResume() {
         super.onResume()
         registerReceiver(nfcStateReceiver, IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED))


     }

     override fun onPause() {
         super.onPause()
         unregisterReceiver(nfcStateReceiver)
         nfcAdapter?.disableReaderMode(this)
     }

     private val nfcStateReceiver = object : BroadcastReceiver() {
         override fun onReceive(context: Context?, intent: Intent?) {
             if (NfcAdapter.ACTION_ADAPTER_STATE_CHANGED == intent?.action) {
                 checkNfcStatus()
             }
         }
     }

     abstract fun checkNfcStatus()
 }