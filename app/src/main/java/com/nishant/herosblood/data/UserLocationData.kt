package com.nishant.herosblood.data

data class UserLocationData(
    val userId: String,
    val latitude: String,
    val longitude: String,
    val addressLine: String,
    val locality: String,
    val state: String,
    val country: String,
    val pincode: String,
    val featureName: String
)