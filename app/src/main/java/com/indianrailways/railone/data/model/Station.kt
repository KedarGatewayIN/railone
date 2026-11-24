package com.indianrailways.railone.data.model

data class Station(
    val code: String,
    val name: String,
    val state: String = "India",
    val zone: String = "IR"
) {
    val displayName: String
        get() = "$name ($code)"
}
