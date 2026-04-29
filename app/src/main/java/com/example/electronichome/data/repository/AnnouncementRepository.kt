package com.example.electronichome.data.repository

import com.example.electronichome.di.ApiClient
import com.example.electronichome.domain.model.AnnouncementResponse
import com.example.electronichome.domain.model.ApiResponse
import io.ktor.client.call.*
import io.ktor.client.request.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnnouncementRepository @Inject constructor() {

    suspend fun getAnnouncements(category: String? = null): Result<List<AnnouncementResponse>> =
        runCatching {
            val resp: ApiResponse<List<AnnouncementResponse>> = ApiClient.client
                .get("${ApiClient.BASE_URL}/announcements") {
                    if (category != null) parameter("category", category)
                }.body()
            resp.data ?: emptyList()
        }
}