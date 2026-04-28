package com.example.electronichome.domain.model

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class MeterReadingRequest(
    val apartmentId: String,
    val month: Int,
    val year: Int,
    val hotWater: Double? = null,
    val coldWater: Double? = null,
    val heating: Double? = null,
    val elecDay: Double? = null,
    val elecNight: Double? = null,
    val elecPeak: Double? = null
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class MeterReadingResponse(
    val id: String,
    val apartmentId: String,
    val month: Int,
    val year: Int,
    val hotWater: Double? = null,
    val coldWater: Double? = null,
    val heating: Double? = null,
    val elecDay: Double? = null,
    val elecNight: Double? = null,
    val elecPeak: Double? = null,
    val createdAt: String,
    val updatedAt: String
)