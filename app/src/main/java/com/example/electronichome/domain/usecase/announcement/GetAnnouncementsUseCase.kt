package com.example.electronichome.domain.usecase.announcement

import com.example.electronichome.data.repository.AnnouncementRepositoryImpl
import com.example.electronichome.domain.model.AnnouncementResponse
import com.example.electronichome.domain.repository.AnnouncementRepository
import javax.inject.Inject

class GetAnnouncementsUseCase @Inject constructor(
    private val repository: AnnouncementRepository
) {
    suspend operator fun invoke(category: String? = null): Result<List<AnnouncementResponse>> =
        repository.getAnnouncements(category)
}