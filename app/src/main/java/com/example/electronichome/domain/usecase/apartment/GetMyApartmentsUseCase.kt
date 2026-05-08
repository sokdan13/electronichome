package com.example.electronichome.domain.usecase.apartment

import com.example.electronichome.data.repository.ApartmentRepository
import com.example.electronichome.domain.model.ApartmentResponse
import javax.inject.Inject

class GetMyApartmentsUseCase @Inject constructor(
    private val repository: ApartmentRepository
) {
    suspend operator fun invoke(): Result<List<ApartmentResponse>> =
        repository.getMyApartments()
}