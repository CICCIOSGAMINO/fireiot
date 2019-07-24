package com.ciccio.fireiot

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.ciccio.fireiot.objects.HwConfigObject
import com.ciccio.fireiot.objects.PubConfigObject
import com.google.android.things.device.TimeManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

private val TAG = MainActivity::class.java.simpleName

class MainActivity : Activity() {

    // Handle the date & time
    private val timeManager : TimeManager = TimeManager.getInstance()
    // Fireiot
    private lateinit var fireiot: Fireiot

    /**
     * Define the configHwValueEventListener, this is a Firebase listener
     * for the Device Hw Config Object
     */
    val configHwValueEventListener: ValueEventListener = object: ValueEventListener {
        override fun onDataChange(snap: DataSnapshot) {

            // Check if no data
            if(snap.child("a").value == true){

                val hw = snap.getValue(HwConfigObject::class.java)
                Log.d(TAG, "@MSG >> CONFIG_HW : ${hw}")

                // Config Hw Updated
                fireiot.configHwUpdated()
            }
        }

        override fun onCancelled(snap: DatabaseError) {
            Log.d(TAG, "@CANCELLED >> CONFIG_HW: ${snap}")
        }
    }


    /**
     * Define the configPubValueEventListener, this is a Firebase listener
     * for the Device Pub Config Object
     */
    val configPubValueEventListener: ValueEventListener = object: ValueEventListener {
        override fun onDataChange(snap: DataSnapshot) {
            // Check if "a" field on true (update flag)
            if(snap.child("a").value == true){

                val pub = snap.getValue(PubConfigObject::class.java)
                Log.d(TAG, "@MSG >> CONFIG_PUB : ${pub}")

                // Config Pub Updated
                fireiot.configPubUpdated()
            }
        }

        override fun onCancelled(snap: DatabaseError) {
            Log.d(TAG, "@CANCELLED >> CONFIG_PUB : ${snap}")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Init the Timezone
        timeManager.setTimeZone("Europe/Rome")

        // Init the Fireiot (example of DeviceID eth iface MAC address) 
        fireiot = Fireiot(
            "B8:27:EB:95:11:EE",
            "wlan0",
            configHwValueEventListener,
            configPubValueEventListener
        )

    }

    override fun onStart() {
        super.onStart()

        fireiot.startOnlineOfflineService()
    }

    override fun onDestroy() {
        super.onDestroy()

    }

}
