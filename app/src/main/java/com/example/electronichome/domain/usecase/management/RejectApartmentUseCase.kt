package com.example.electronichome.domain.usecase.management

import com.example.electronichome.data.repository.ManagementRepository
import com.example.electronichome.domain.model.ApartmentResponse
import javax.inject.Inject

class RejectApartmentUseCase @Inject constructor(
    private val repository: ManagementRepository
) {
    suspend operator fun invoke(id: String, note: String): Result<ApartmentResponse> {
        if (id.isBlank())
            return Result.failure(Exception("ID квартиры не найден"))
        if (note.isBlank())
            return Result.failure(Exception("Укажите причину отклонения"))

        return repository.rejectApartment(id, note)
    }
}