package com.ciccio.fireiot.objects

/**
 * Firebase Hardware status data object, this object is update with the
 * device status (online/offline + TIMESTAMP) in device data block
 */
data class PubConfigObject(
    val a: Boolean = false, // launch new Pub conf
    val aa: Long = 0,            // last updated time
    val cloudRegion: String = "",
    val deviceId: String = "",
    val projectId: String = "",
    val registry: String = "",
    val topic: String = ""
)