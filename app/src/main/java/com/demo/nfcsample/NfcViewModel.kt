package com.demo.nfcsample

import android.app.Application
import android.nfc.NfcAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


/**
 * Created on junio.
 * year 2024 .
 */
class NfcViewModel(application: Application) : AndroidViewModel(application) {
    private val _nfcStatus = MutableLiveData<NfcStatus>()
    val nfcStatus: LiveData<NfcStatus> = _nfcStatus

    private var nfcAdapter: NfcAdapter? = null


    fun getNfcAdapter(): NfcAdapter? {
        return NfcAdapter.getDefaultAdapter(getApplication())
    }

    fun checkNfcStatus() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(getApplication())
        if (nfcAdapter == null) {
            _nfcStatus.value = NfcStatus.NOT_SUPPORTED
        } else if (!nfcAdapter!!.isEnabled) {
            _nfcStatus.value = NfcStatus.DISABLED
        } else {
            _nfcStatus.value = NfcStatus.ENABLED
        }
    }
}

enum class NfcStatus {
    ENABLED,
    DISABLED,
    NOT_SUPPORTED
}