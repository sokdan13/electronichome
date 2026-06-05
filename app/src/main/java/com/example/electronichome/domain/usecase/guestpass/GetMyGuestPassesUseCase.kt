package com.example.electronichome.domain.usecase.guestpass

import com.example.electronichome.data.repository.GuestPassRepositoryImpl
import com.example.electronichome.domain.model.GuestPassResponse
import com.example.electronichome.domain.repository.GuestPassRepository
import javax.inject.Inject

class GetMyGuestPassesUseCase @Inject constructor(
    private val repository: GuestPassRepository
) {
    suspend operator fun invoke(): Result<List<GuestPassResponse>> =
        repository.getMyPasses()
}