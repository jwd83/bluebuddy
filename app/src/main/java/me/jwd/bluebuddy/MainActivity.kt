package me.jwd.bluebuddy

import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    lateinit var m_bluetoothAdapter: BluetoothAdapter
    lateinit var m_pairedDevices: Set<BluetoothDevice>
    val REQUEST_ENABLE_BLUETOOTH = 1

    companion object {
        // used as key for intent extras
        val EXTRA_ADDRESS: String = "Device_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // assign our bluetooth adapter
        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // make sure this is not null.
        // if this device doesn't support bluetooth we want to notify them or something and exit

        if(m_bluetoothAdapter == null) {
            toast("This device doesn't support bluetooth.")
            return
        } else {
            toast("This device supports bluetooth.")
        }
    }

    private fun pairedDeviceList() {

    }

    /**
     * for when we go to enable bluetooth and come back
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

}
