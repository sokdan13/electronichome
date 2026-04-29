package com.example.electronichome.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AnnouncementResponse(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val categoryLabel: String,
    val imageUrl: String? = null,
    val createdAt: String,
    val updatedAt: String
)

enum class AnnouncementCategoryUi(
    val key: String,
    val label: String,
) {
    ALL("ALL",           "Все"),
    IMPORTANT("IMPORTANT", "Важно"),
    NEWS("NEWS",         "Новости"),
    TIPS("TIPS",         "Советы")
}