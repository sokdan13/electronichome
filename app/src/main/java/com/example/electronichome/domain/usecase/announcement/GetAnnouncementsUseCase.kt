package com.example.electronichome.domain.usecase.announcement

import com.example.electronichome.data.repository.AnnouncementRepository
import com.example.electronichome.domain.model.AnnouncementResponse
import javax.inject.Inject

class GetAnnouncementsUseCase @Inject constructor(
    private val repository: AnnouncementRepository
) {
    suspend operator fun invoke(category: String? = null): Result<List<AnnouncementResponse>> =
        repository.getAnnouncements(category)
}