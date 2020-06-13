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
        var m_inputBuffer: String = ""
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_layout)
        m_address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS)

        ConnectToDevice(this, this).execute()

        buttonSetDAC1.setOnClickListener {
            sendCommand("$1" + computeDACValue(editTextDAC1.text.toString().toDouble()))
        }

        buttonSetDAC2.setOnClickListener {
            sendCommand("$2" + computeDACValue(editTextDAC2.text.toString().toDouble()))
        }

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
                val stream = m_bluetoothSocket!!.inputStream
                val available = stream.available()
                // check if data is available
                if (available > 0) {
                    val bytes = ByteArray(available)
                    stream.read(bytes, 0, available)
                    val str = String(bytes)
//                    toast("Available: ${available}\nDATA:\n$str")
                    receiveCommand(str)
                } else {
                    toast("Empty")
                }
            }
        }
    }

    private fun computeDACValue(voltage: Double): String {
        val DACOutInt: Int = (voltage.coerceIn(0.0, 3.3) / 3.3 * 255).toInt().coerceIn(0, 255)
        return DACOutInt.toString().padStart(3, '0')
    }

    private fun receiveCommand(input: String) {
        var validCommand: Boolean = false
        val hasColon: Boolean = input.contains(':')
        val hasBreak: Boolean = input.contains("\n")

        if(hasColon && hasBreak) {
            val parts = input.split(":")
            if (parts.count() == 2) {
                validCommand = true
                var values = parts[1].trim().split(",")

                when(parts[0]) {
                    "a", "A", "v", "V" -> {
                        if(values.count() == 6) {
                            var suffix = if (parts[0].toUpperCase() == "A") {
                                " ADC"
                            } else {
                                " Volts"
                            }
                            text_analog_voltage_1.text = values[0] + suffix
                            text_analog_voltage_2.text = values[1] + suffix
                            text_analog_voltage_3.text = values[2] + suffix
                            text_analog_voltage_4.text = values[3] + suffix
                            text_analog_voltage_5.text = values[4] + suffix
                            text_analog_voltage_6.text = values[5] + suffix
                        }
                    }

                    "DAC" -> {
                        if(values.count() == 2) {
                            if(values[0] == "1") editTextDAC1.setText(values[1])
                            if(values[0] == "2") editTextDAC1.setText(values[1])
                        }
                    }
//                    "v" -> {
//                        if(values.count() == 6) {
//                            text_analog_voltage_1.text = values[0]
//                            text_analog_voltage_2.text = values[1]
//                            text_analog_voltage_3.text = values[2]
//                            text_analog_voltage_4.text = values[3]
//                            text_analog_voltage_5.text = values[4]
//                            text_analog_voltage_6.text = values[5]
//                        }
//                    }
                    else -> {
                        toast("Unrecognized command: ${parts[0]}\nData: ${parts[1]}")
                    }
                }
            }
        }

        if(!validCommand){
            toast("Rejected command: ${input}")
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

                // start thread to sample values that calls the UI thread to update values
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