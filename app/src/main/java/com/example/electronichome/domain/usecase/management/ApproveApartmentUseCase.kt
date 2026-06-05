package com.example.electronichome.domain.usecase.management

import com.example.electronichome.data.repository.ManagementRepositoryImpl
import com.example.electronichome.domain.model.ApproveRequest
import com.example.electronichome.domain.model.ApartmentResponse
import com.example.electronichome.domain.repository.ManagementRepository
import javax.inject.Inject

class ApproveApartmentUseCase @Inject constructor(
    private val repository: ManagementRepository
) {
    suspend operator fun invoke(
        id: String,
        req: ApproveRequest
    ): Result<ApartmentResponse> {
        if (id.isBlank())
            return Result.failure(Exception("ID квартиры не найден"))

        return repository.approveApartment(id, req)
    }
}