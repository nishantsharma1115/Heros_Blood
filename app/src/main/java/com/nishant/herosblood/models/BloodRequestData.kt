package com.nishant.herosblood.models

import com.nishant.herosblood.util.location.LocationModel
import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

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
    var locationData: LocationModel = LocationModel(),
    var createdAt: String = SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(Date())
) : Serializable