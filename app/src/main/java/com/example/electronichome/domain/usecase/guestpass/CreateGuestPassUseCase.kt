package com.example.electronichome.domain.usecase.guestpass

import com.example.electronichome.data.repository.GuestPassRepositoryImpl
import com.example.electronichome.domain.model.GuestPassCreateDto
import com.example.electronichome.domain.model.GuestPassResponse
import com.example.electronichome.domain.repository.GuestPassRepository
import javax.inject.Inject

class CreateGuestPassUseCase @Inject constructor(
    private val repository: GuestPassRepository
) {
    suspend operator fun invoke(dto: GuestPassCreateDto): Result<GuestPassResponse> {
        if (dto.apartmentId.isBlank())
            return Result.failure(Exception("Квартира не выбрана"))
        if (dto.durationMinutes !in listOf(5, 30, 60))
            return Result.failure(Exception("Недопустимый срок действия"))

        return repository.createPass(dto)
    }
}