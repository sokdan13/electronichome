package com.example.electronichome.domain.usecase.management

import com.example.electronichome.data.repository.ManagementRepositoryImpl
import com.example.electronichome.domain.model.RequestResponse
import com.example.electronichome.domain.repository.ManagementRepository
import javax.inject.Inject

class RejectRequestUseCase @Inject constructor(
    private val repository: ManagementRepository
) {
    suspend operator fun invoke(id: String): Result<RequestResponse> {
        if (id.isBlank())
            return Result.failure(Exception("ID заявки не найден"))

        return repository.rejectRequest(id)
    }
}