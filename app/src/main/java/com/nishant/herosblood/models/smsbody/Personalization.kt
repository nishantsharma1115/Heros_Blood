package com.nishant.herosblood.models.smsbody

data class Personalization(
    val subject: String,
    val to: List<To>
)