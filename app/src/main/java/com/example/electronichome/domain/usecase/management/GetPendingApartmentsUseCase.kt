package com.example.electronichome.domain.usecase.management

import com.example.electronichome.data.repository.ManagementRepositoryImpl
import com.example.electronichome.domain.model.ApartmentResponse
import com.example.electronichome.domain.repository.ManagementRepository
import javax.inject.Inject

class GetPendingApartmentsUseCase @Inject constructor(
    private val repository: ManagementRepository
) {
    suspend operator fun invoke(): Result<List<ApartmentResponse>> =
        repository.getPendingApartments()
}