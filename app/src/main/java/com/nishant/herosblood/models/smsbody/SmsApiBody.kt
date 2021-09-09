package com.nishant.herosblood.models.smsbody

data class SmsApiBody(
    val content: List<Content>,
    val from: From,
    val personalizations: List<Personalization>,
    val reply_to: ReplyTo
)