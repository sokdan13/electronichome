package com.example.electronichome.domain.model

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class RequestCreateDto(
    val apartmentId: String,
    val category: String,
    val description: String? = null
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class RequestResponse(
    val id: String,
    val userId: String,
    val apartmentId: String,
    val category: String,
    val categoryLabel: String,
    val description: String? = null,
    val status: String,
    val statusLabel: String,
    val dueDate: String? = null,
    val createdAt: String,
    val updatedAt: String
)

enum class RequestCategoryUi(val key: String, val label: String) {
    GARBAGE("GARBAGE",        "Некачественное содержание (мусор)"),
    HOT_COLD_WATER("HOT_COLD_WATER", "Неисправность ГВС/ХВС"),
    ELECTRICITY("ELECTRICITY", "Неисправность электричества"),
    ELEVATOR("ELEVATOR",       "Проблемы с лифтом"),
    CHUTE("CHUTE",             "Засор в мусоропроводе")
}