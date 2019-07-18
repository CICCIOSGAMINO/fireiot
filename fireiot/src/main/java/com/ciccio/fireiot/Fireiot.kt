package com.ciccio.fireiot

import android.util.Log
import com.ciccio.fireiot.objects.ErrorLogObject
import com.ciccio.fireiot.objects.OnlineOfflineObject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

private val TAG = Fireiot::class.java.simpleName

class Fireiot(
    val macAddrs: String,
    val iface: String,
    configHwValueEventListener: ValueEventListener,
    configPubValueEventListener: ValueEventListener
) {

    // TODO - TEST RealTime DB Vs Firestore

    // Firebase vars
    private val auth: FirebaseAuth
    private val db : FirebaseDatabase

    // Firebase RelaTime DB references
    private val fireStatusRef: DatabaseReference
    private val deviceRef: DatabaseReference
    private val deviceStatusRef: DatabaseReference
    private val deviceErrorRef: DatabaseReference
    private val configHwRef: DatabaseReference
    private val configPubRef: DatabaseReference
    private val pubKeyRef: DatabaseReference

    /**
     * Object to reference Online/Offline data structure in RealTime DB
     */
    val isOnlineForDatabase = OnlineOfflineObject(
        ServerValue.TIMESTAMP,
        iface,
        "online"
    )

    val isOfflineForDatabase = OnlineOfflineObject(
        ServerValue.TIMESTAMP,
        iface,
        "offline"
    )

    // Initialize the FirebaseAuth in the onCreate() method
    init {

        // Initialize Firebase stuff
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        /** When you enable disk persistence, your app writes the data locally to
         * the device so your app can maintain state while offline, even if
         * the user or operating system restarts the app. */
        db.setPersistenceEnabled(true)

        // Reference to Firebase Online/Offline service
        fireStatusRef = db.getReference(".info/connected")
        // Device Ref
        deviceRef = db.getReference("/$macAddrs")
        // Ref to device specific status, is it online/offline
        deviceStatusRef = db.getReference("/$macAddrs/status")
        // Ref to Device Hw Config
        configHwRef = db.getReference("/$macAddrs/config/hw")
        // Ref to Pub/Sub Message Infrastructure Config
        configPubRef = db.getReference("/$macAddrs/config/pub")
        // Ref to Device PubKey Reference
        pubKeyRef = db.getReference("/$macAddrs/config/pub_key")
        // Ref Project errors data space
        deviceErrorRef = db.getReference("/$macAddrs/errors/")

        authenticate()

        // Attach changes listening for Device Config Hw Object
        configHwRef.addValueEventListener(configHwValueEventListener)
        // Attach changes listening for Device Config Pub Object
        configPubRef.addValueEventListener(configPubValueEventListener)
    }

    /**
     * Define the Firebase Online/Offline ValueEventListener, this listening
     * for Online/Offline connection to Firebase state
     */
    private val statusEventListener : ValueEventListener = object : ValueEventListener {
        override fun onDataChange(snap: DataSnapshot) {
            if(snap.value === true) {
                // Connected/Online
                deviceStatusRef.setValue(isOnlineForDatabase)
            } else {
                // UnConnected/Offline
                deviceStatusRef.onDisconnect().setValue(isOfflineForDatabase)
            }
        }

        override fun onCancelled(error : DatabaseError) {
            Log.d(TAG, "@MSG >> -Online/Offline_Listener_CANCELLED")
        }
    }

    /**
     * Start to Listen at Firebase .info/connected value, this is a
     * good estimator for the Firebase connection status
     */
    fun startOnlineOfflineService() {
        fireStatusRef.addValueEventListener(statusEventListener)
    }

    /**
     * Detach Listeners for Online/Offline Firebase Service
     */
    fun stopFireOnlineOfflineService() {
        fireStatusRef.removeEventListener(statusEventListener)
    }


    /**
     * Custom Authenticate method on Firebase, for *@logicatsrl.eu devices
     */
    private fun authenticate() {
        auth.signInWithEmailAndPassword("${macAddrs.replace(":","")}@logicatsrl.eu", macAddrs)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    // Login success
                    Log.d(TAG, "@MSG >> Logged $macAddrs@logicatsrl.eu")
                } else {
                    // Login Fail
                    Log.d(TAG, "@MSG >> Login FAIL")
                }
            }
    }

    /**
     * Helper function to get the Logged user in Firebase env
     * @return FirebaseUser logged in
     */
    fun user(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Save/Uodate the Pub key of the device, key is created every time the .apk
     * is installed.
     * @param pubKey String Public key in PEM String format
     */
    fun savePubKey(pubKey: String) {
        pubKeyRef.setValue(pubKey)
    }

    /**
     * Log the device last error, in the errorsRef
     * @param error String Text error to log in
     */
    fun logError(error: String) {
        deviceErrorRef.setValue(
            ErrorLogObject(error, ServerValue.TIMESTAMP)
        )
    }

    /**
     * Device config Pub are updated log in the Firebase db conf hw a field
     */
    fun configHwUpdated() {
        configHwRef.child("a").setValue(false)
        configHwRef.child("aa").setValue(ServerValue.TIMESTAMP)
    }

    /**
     * Device config Pub are updated log in the Firebase db conf pub a field
     */
    fun configPubUpdated() {
        configPubRef.child("a").setValue(false)
        configPubRef.child("aa").setValue(ServerValue.TIMESTAMP)
    }

}