package com.ciccio.fireiot.objects

/**
 * Firebase device status data object, this object is update with the
 * device status (online/offline + TIMESTAMP) in device data block
 */
data class OnlineOfflineObject(
    val a: Any,
    val iface : String = "",
    val state : String = ""
)