package com.nishant.herosblood.data

data class UserData(
    var userId: String,
    var email: String,
    var phoneNumber: String? = null,
    var isRegistered: Boolean,
    var name: String,
    var bloodType: String? = null,
    var address: String? = null,
    var state: String? = null,
    var city: String? = null,
    var pincode: String? = null
)