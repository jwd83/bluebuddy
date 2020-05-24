package me.jwd.bluebuddy

import android.app.Activity
import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    var m_bluetoothAdapter: BluetoothAdapter? = null
    lateinit var m_pairedDevices: Set<BluetoothDevice>
    val REQUEST_ENABLE_BLUETOOTH = 1

    companion object {
        // used as key for intent extras
        val EXTRA_ADDRESS: String = "Device_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * attempt to create our bluetooth adapter
         */
        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        /**
         * make sure bluetooth adapter is not null.
         * if this device doesn't support bluetooth we want to notify them or something and exit
         */
        if(m_bluetoothAdapter == null) {
            toast("This device doesn't support bluetooth.")
            return
        }

        /**
         * request bluetooth permissions
         */
        if(!m_bluetoothAdapter!!.isEnabled) {
            val eBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(eBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }

        /**
         * setup the button to list devices
         */
        buttonListDevices.setOnClickListener { pairedDeviceList() }
    }

    private fun pairedDeviceList() {
        toast("Generating paired device list")

        /**
         * look for bonded devices
         */
        m_pairedDevices = m_bluetoothAdapter!!.bondedDevices
        val list : ArrayList<BluetoothDevice> = ArrayList()

        /**
         * make sure we have data go to through
         */
        if(!m_pairedDevices.isEmpty()) {
            for(device: BluetoothDevice in m_pairedDevices)
            {
                list.add(device)
                Log.i("device", ""+device)
            }
        } else {
            toast("No paired devices.")
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            list
        )

        select_device_list.adapter = adapter
        select_device_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device: BluetoothDevice = list[position]
            val mac_address: String = device.address

            val intent = Intent(this, ControlActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS, mac_address)
            startActivity(intent)
        }
    }

    /**
     * for when we go to enable bluetooth and come back
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /**
         * check if this is being called by our enable bluetooth request
         */
        if(requestCode == REQUEST_ENABLE_BLUETOOTH) {
            /**
             * check if this went OK
             */
            if(resultCode == Activity.RESULT_OK) {
                /**
                 * check if adapter is enabled and display a message
                 */
                if(m_bluetoothAdapter!!.isEnabled) {
                    toast("Bluetooth has been enabled")
                } else {
                    toast("Bluetooth has been disabled")
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                /**
                 * if this was canceled notify bluetooth enabling has been canceled.
                 */
                toast("Bluetooth enabling has been canceled")
            }
        }
    }
}
