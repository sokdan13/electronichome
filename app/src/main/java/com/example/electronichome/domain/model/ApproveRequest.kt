package com.example.electronichome.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ApproveRequest(
    val city: String? = null,
    val street: String? = null,
    val house: String? = null,
    val building: String? = null,
    val floor: Int? = null,
    val apartment: String? = null,
    val accountNumber: String? = null
)

@Serializable
data class RejectRequest(
    val note: String
)

@Serializable
data class TakeInProgressDto(
    val dueDate: String
)