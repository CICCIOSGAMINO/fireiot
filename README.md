Fireiot
=======
[TOC]

Helper Firebase module to use with a custom Firebase IoT platform. The Firebase IoT platform is responsable 
to handle the online/offline status of the IoT devices as the Hardware / Google Cloud IoT configuration of the 
devices. 

## Firebase Dependancies 
Keep in mind to add all the Firebase -> Android dependancies and download from Firebase Console the google-service.json
to activate the Android App on Firebase platform. 

build.gradle  (Project)
```gradle
dependencies {
        ...
        classpath 'com.google.gms:google-services:4.2.0' // google-services plugin
    }
```

build.gradle  (App)
```gradle
// Firebase stuff
implementation 'com.google.firebase:firebase-core:xx.xx.xx'
implementation 'com.google.firebase:firebase-auth:xx.xx.xx'
implementation 'com.google.firebase:firebase-database:xx.xx.xx'
```

## Activity 
Import the package as dependencies and init the variables you need in the activity, here an example 
of use: 

```kotlin
class MainActivity : Activity() {

    private lateinit var fireIot: Fireiot

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
                fireIot.configHwUpdated()
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
                fireIot.configPubUpdated()
            }
        }

        override fun onCancelled(snap: DatabaseError) {
            Log.d(TAG, "@CANCELLED >> CONFIG_PUB : ${snap}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Init the Fireiot (Example of DeviceID as ETH iface MAC address )
        fireIot = Fireiot(
            "B8:27:EB:95:11:EE",
            "wlan0",
            configHwValueEventListener,
            configPubValueEventListener
        )
    }

    override fun onStart() {
        super.onStart()

        fireIot.startOnlineOfflineService()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
```

Here a simple example of Activity where the Fireiot module is used to gathering the Firebase 
IoT services. In this simple example as DeviceID we have used the ETH iface MAC address, but you 
can pass the String value you want to handle your IoT devices. 


