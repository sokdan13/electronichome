package com.example.electronichome.domain.repository

import com.example.electronichome.domain.model.AnnouncementResponse

interface AnnouncementRepository {

    suspend fun getAnnouncements(
        category: String? = null
    ): Result<List<AnnouncementResponse>>
}