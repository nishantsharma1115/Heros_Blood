package com.nishant.herosblood.models

import java.io.Serializable

data class UserData(
    var userId: String? = null,
    var email: String? = null,
    var phoneNumber: String? = null,
    var registered: String? = null,
    var name: String? = null,
    var bloodGroup: String? = null,
    var address: String? = null,
    var state: String? = null,
    var city: String? = null,
    var pincode: String? = null,
    var fullAddress: String? = null,
    var profilePictureUrl: String? = null
) : Serializable