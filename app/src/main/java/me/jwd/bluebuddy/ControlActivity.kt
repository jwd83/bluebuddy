package me.jwd.bluebuddy

import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.control_layout.*
import org.jetbrains.anko.doAsyncResult
import java.util.*
import org.jetbrains.anko.toast
import java.io.IOException
import kotlin.reflect.typeOf


class ControlActivity: AppCompatActivity(), AsyncResponse {

    companion object {
        // 00001101-0000-1000-8000-00805F9B34FB // Standard SerialPortService ID
        // cfde2417-b4de-4727-9b30-788261fedfaf // generated (not working)
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_layout)
        m_address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS)

        ConnectToDevice(this, this).execute()

        control_led_on.setOnClickListener {
            sendCommand("a")

        }

        control_led_off.setOnClickListener {
            sendCommand("v")
        }

        control_disconnect.setOnClickListener {
            disconnect()
        }

        control_receive.setOnClickListener {
            /**
             * see if we can get data from here
             */
            if(m_bluetoothSocket != null) {
                val instream = m_bluetoothSocket!!.inputStream
                if (instream.available() > 0) {
                    toast(instream.read().toString())
                }
            }
        }
    }

    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            try{
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun disconnect() {
        if(m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        finish()
    }

    private class ConnectToDevice(c: Context, r: AsyncResponse) : AsyncTask<Void, Void, String>() {

        private var connectSuccess: Boolean = true
        private val context: Context = c
        private val ar: AsyncResponse = r

        override fun doInBackground(vararg params: Void?): String? {
//            TODO("Not yet implemented")
            try {
                /**
                 * create / establish connection
                 */
                if(m_bluetoothSocket == null || !m_isConnected) {
                    Log.i("data","Connecting to $m_address")
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()
                }
//
//                /**
//                 * check for data
//                 */
//                if(m_bluetoothSocket != null) {
//                    var b: ByteArray = ByteArray(8192)
//                    m_bluetoothSocket!!.inputStream.read(b)
//                    var s = b.toString()
//                    Log.i("data", "received data len ${s.length}: ${s}")
//
//                }

            } catch (e: IOException) {
                Log.i("data", "Catch: \n" + e.cause.toString() + "\n\n" + e.message.toString())
//                ar.response(, false)
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context, "Connecting...", "Please wait for connection")
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(!connectSuccess) {
                Log.i("data", "could not connect")
            } else {
                m_isConnected = true
            }

            m_progress.dismiss()

            if(m_isConnected) {
                ar.response("Connected!", false)
            } else {
                ar.response("Could not connect.\n\nPlease hang up and try again.", true)
            }
        }
    }

    override fun response(output: String, exit: Boolean) {
        toast(output)
        if(exit) finish()
    }
}