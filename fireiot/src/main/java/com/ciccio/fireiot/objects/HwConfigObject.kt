package com.ciccio.fireiot.objects

/**
 * Firebase Hardware status data object, this object is update with the
 * device status (online/offline + TIMESTAMP) in device data block
 */
data class HwConfigObject(
    val a: Boolean = false, // launch new Hw conf
    val aa: Long = 0,            // last updated time
    val board: String = "",
    val pins: Object? = null
)