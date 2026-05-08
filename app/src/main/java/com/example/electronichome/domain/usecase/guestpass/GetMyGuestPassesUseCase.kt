package com.example.electronichome.domain.usecase.guestpass

import com.example.electronichome.data.repository.GuestPassRepository
import com.example.electronichome.domain.model.GuestPassResponse
import javax.inject.Inject

class GetMyGuestPassesUseCase @Inject constructor(
    private val repository: GuestPassRepository
) {
    suspend operator fun invoke(): Result<List<GuestPassResponse>> =
        repository.getMyPasses()
}