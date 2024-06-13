# NFC-Sample
NFC
Format NDEF Reader and Writer 
 
AGP 7.4.0
gradle 7.5

# Permission
using of NfcAdapter.ReaderCallback support 19 min api
 <uses-permission android:name="android.permission.NFC" />
 <uses-feature android:name="android.hardware.nfc" android:required="true" />

# Description
NFC (Near Field Communication) is a wireless technology that allows data to be exchanged between devices that are close to each other. It is a set of communication protocols that enable two electronic devices, one of which is usually a portable device such as a smartphone, to establish communication by bringing them within 4 cm (1.6 in) of each other.
NFC Sample is a simple android application that demonstrates how to read and write NFC tags. The application has two activities, one for reading NFC tags and the other for writing NFC tags. The application uses the NfcAdapter class to interact with the NFC hardware on the device. The NfcAdapter class provides methods for reading and writing NFC tags, as well as for enabling and disabling the NFC hardware on the device.


# Useful links
https://developer.android.com/develop/connectivity/nfc/advanced-nfc#kotlin
https://stackoverflow.com/questions/75695662/securityexception-while-ndef-connect-on-android-13
https://stackoverflow.com/a/76430994/5636211
https://stackoverflow.com/a/77405882/5636211