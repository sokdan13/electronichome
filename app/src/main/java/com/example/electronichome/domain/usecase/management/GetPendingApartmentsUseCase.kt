package com.example.electronichome.domain.usecase.management

import com.example.electronichome.data.repository.ManagementRepository
import com.example.electronichome.domain.model.ApartmentResponse
import javax.inject.Inject

class GetPendingApartmentsUseCase @Inject constructor(
    private val repository: ManagementRepository
) {
    suspend operator fun invoke(): Result<List<ApartmentResponse>> =
        repository.getPendingApartments()
}