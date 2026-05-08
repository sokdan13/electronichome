package com.example.electronichome.domain.usecase.apartment

import com.example.electronichome.data.repository.ApartmentRepository
import com.example.electronichome.domain.model.ApartmentRequest
import com.example.electronichome.domain.model.ApartmentResponse
import javax.inject.Inject

class AddApartmentUseCase @Inject constructor(
    private val repository: ApartmentRepository
) {
    suspend operator fun invoke(request: ApartmentRequest): Result<ApartmentResponse> {
        if (request.city.isBlank())
            return Result.failure(Exception("Укажите город"))
        if (request.street.isBlank())
            return Result.failure(Exception("Укажите улицу"))
        if (request.house.isBlank())
            return Result.failure(Exception("Укажите номер дома"))
        if (request.apartment.isBlank())
            return Result.failure(Exception("Укажите номер квартиры"))
        if (request.floor < 1 || request.floor > 200)
            return Result.failure(Exception("Некорректный этаж"))

        return repository.addApartment(request)
    }
}