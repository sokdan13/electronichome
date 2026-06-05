package com.example.electronichome.domain.usecase.apartment

import com.example.electronichome.data.repository.ApartmentRepositoryImpl
import com.example.electronichome.domain.model.ApartmentResponse
import com.example.electronichome.domain.repository.ApartmentRepository
import javax.inject.Inject

class GetMyApartmentsUseCase @Inject constructor(
    private val repository: ApartmentRepository
) {
    suspend operator fun invoke(): Result<List<ApartmentResponse>> =
        repository.getMyApartments()
}