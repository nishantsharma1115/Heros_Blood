package com.nishant.herosblood.models

import java.io.Serializable

data class BloodRequestData(
    var userId: String? = null,
    var email: String? = null,
    var phoneNumber: String? = null,
    var name: String? = null,
    var bloodGroup: String? = null,
    var address: String? = null,
    var weight: String? = null,
    var state: String? = null,
    var city: String? = null,
    var pincode: String? = null,
    var fullAddress: String? = null,
    var unitOfBloodRequired: String? = "0"
) : Serializable