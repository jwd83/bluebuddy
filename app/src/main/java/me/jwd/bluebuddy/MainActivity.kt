package me.jwd.bluebuddy

import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
//
//        var b: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//        var t: BluetoothProfile
//
//        buttonListDevices.setOnClickListener {
//            var txt = "Checking default adapter\n\n"
//
//            if(b.isEnabled) {
//                txt += "Enabled"
//            } else {
//                txt += "Disabled"
//            }
//
////
////            var txt: String = "Connected devices...\n"
////            txt += "\n"
//////            var lst = BluetoothProfile
////            t = b.
////            t.connectedDevices.forEach(
////                it.
////            )
//
//            textViewStatus.text = txt
//        }
        return super.onCreateView(name, context, attrs)
    }

}
