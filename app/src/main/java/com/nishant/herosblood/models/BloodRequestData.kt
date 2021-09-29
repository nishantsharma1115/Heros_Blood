package com.nishant.herosblood.models

import com.nishant.herosblood.util.location.LocationModel
import java.io.Serializable

data class BloodRequestData(
    var bloodGroup: String? = null,
    var unitOfBloodRequired: String? = "0",
    var isCritical: String = "false",
    var firstName: String? = null,
    var lastName: String? = null,
    var fullName: String? = null,
    var phoneNumber: String? = null,
    var address: String? = null,
    var userId: String? = null,
    var locationData: LocationModel? = null,
    var createdAt: String = System.currentTimeMillis().toString()
) : Serializable