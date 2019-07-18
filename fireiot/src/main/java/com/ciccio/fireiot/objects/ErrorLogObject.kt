package com.ciccio.fireiot.objects

/**
 * Firebase error log object, this object contain on the firebase
 * db the last device error
 */
data class ErrorLogObject(
    val msg : String = "",
    val last_changed : Any
)