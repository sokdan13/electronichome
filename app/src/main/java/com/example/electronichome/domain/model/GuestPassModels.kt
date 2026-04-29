package com.example.electronichome.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class GuestPassCreateDto(
    val apartmentId: String,
    val durationMinutes: Int
)

@Serializable
data class GuestPassResponse(
    val id: String,
    val apartmentId: String,
    val token: String,
    val expiresAt: String,
    val createdAt: String,
    val isValid: Boolean,
    val minutesLeft: Long
)

enum class PassDuration(val minutes: Int, val label: String) {
    FIVE(5,   "5 минут"),
    THIRTY(30, "30 минут"),
    HOUR(60,  "1 час")
}