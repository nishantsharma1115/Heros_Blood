package com.nishant.herosblood.api

import com.nishant.herosblood.models.smsbody.SmsApiBody
import com.nishant.herosblood.util.Constants
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface SmsApi {

    @Headers(
        "Authorization: Bearer ${Constants.API_KEY}",
        "Content-Type: application/json"
    )
    @POST("mail/send")
    suspend fun sendVerificationMail(
        @Body smsApiBody: SmsApiBody
    )
}