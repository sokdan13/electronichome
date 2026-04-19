package com.example.electronichome.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ApartmentRequest(
    val city: String,
    val street: String,
    val house: String,
    val building: String? = null,
    val floor: Int,
    val apartment: String
)

@Serializable
data class ApartmentResponse(
    val id: String,
    val userId: String,
    val city: String,
    val street: String,
    val house: String,
    val building: String? = null,
    val floor: Int,
    val apartment: String,
    val status: String,
    val accountNumber: String? = null,
    val rejectionNote: String? = null,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null
)