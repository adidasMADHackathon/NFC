package com.adidas.sanzalb.nfcsample

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import java.io.IOException


class MainActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {


    companion object {
        const val READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
        val MIFARE_CLASSIC = "android.nfc.tech.MifareClassic"
        val MIFARE_ULTRALIGHT = "android.nfc.tech.MifareUltralight"
    }


    private val nfcAdapter by lazy { NfcAdapter.getDefaultAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        if (!nfcAdapter.isEnabled) {
            AlertDialog.Builder(this).apply {
                setTitle("Error")
                setMessage("Please, enable NFC")
                setPositiveButton("Turn On", { _, _ ->
                    val intent = Intent(Settings.ACTION_NFC_SETTINGS)
                    startActivity(intent)

                })
                show()
            }
        } else {
            nfcAdapter.enableReaderMode(this, this, READER_FLAGS, null)

        }
    }

    override fun onPause() {
        nfcAdapter.disableReaderMode(this)
        super.onPause()
    }

    override fun onTagDiscovered(tag: Tag) {

        if (tag.techList.contains(MIFARE_CLASSIC)) {
            // TODO...
        } else if (tag.techList.contains(MIFARE_ULTRALIGHT)) {
            readUltralight(tag)
        }
    }

    private fun readUltralight(tag: Tag) {
        val ultralightTag = MifareUltralight.get(tag)
        ultralightTag.use { ultralight ->
            ultralight.connect()
            try {
                var page = 0
                while (true) {
                    val asString = String(ultralight.readPages(page++))
                    println(asString)
                }
            } catch (_: IndexOutOfBoundsException) {
            } catch (_: IOException) {
            }
        }

    }

}
