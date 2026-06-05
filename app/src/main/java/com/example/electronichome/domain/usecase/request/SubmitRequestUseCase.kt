package com.example.electronichome.domain.usecase.request

import com.example.electronichome.data.repository.ApartmentRepositoryImpl
import com.example.electronichome.data.repository.RequestRepositoryImpl
import com.example.electronichome.domain.model.RequestCreateDto
import com.example.electronichome.domain.model.RequestResponse
import com.example.electronichome.domain.repository.ApartmentRepository
import com.example.electronichome.domain.repository.RequestRepository
import javax.inject.Inject

class SubmitRequestUseCase @Inject constructor(
    private val requestRepository: RequestRepository,
    private val apartmentRepository: ApartmentRepository
) {
    suspend operator fun invoke(dto: RequestCreateDto): Result<RequestResponse> {
        if (dto.apartmentId.isBlank()) return Result.failure(Exception("Квартира не выбрана"))
        if (dto.category.isBlank()) return Result.failure(Exception("Выберите категорию"))

        val apartments = apartmentRepository.getMyApartments().getOrElse {
            return Result.failure(Exception("Не удалось проверить квартиру"))
        }
        if (apartments.none { it.id == dto.apartmentId }) return Result.failure(Exception("Квартира не найдена"))

        return requestRepository.createRequest(dto)
    }
}