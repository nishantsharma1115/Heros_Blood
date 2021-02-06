package com.nishant.herosblood.data

data class UserLocationData(
    val userId: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    val addressLine: String? = null,
    val locality: String? = null,
    val state: String? = null,
    val country: String? = null,
    val pincode: String? = null,
    val featureName: String? = null
)