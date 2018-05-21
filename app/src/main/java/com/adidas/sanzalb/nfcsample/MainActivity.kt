package com.adidas.sanzalb.nfcsample

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import java.io.IOException
import android.widget.TextView





class MainActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private var textView: TextView? = null
    private var handler: Handler? = null
    private lateinit var mRunnable:Runnable
    private var content = ""


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

        handler = Handler() // Initialize the Handler from the Main Thread

        // Task for updating the UI
        mRunnable = Runnable {
            textView = findViewById<TextView>(R.id.textNFC)
            textView?.text = content
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

    /*
     * Read info from the tag
     */
    private fun readUltralight(tag: Tag) {
        val ultralightTag = MifareUltralight.get(tag)
        ultralightTag.use { ultralight ->
            ultralight.connect()
            try {
                var page = 0
                // Clear the old tag data
                content = ""

                while (page < 35) {
                    val asString = String(ultralight.readPages(page++))
                    content += asString
                    println(asString)
                }

                // Launch the task for updating the UI
                handler?.post(mRunnable)

            } catch (_: IndexOutOfBoundsException) {
            } catch (_: IOException) {
            }
        }
    }
}
