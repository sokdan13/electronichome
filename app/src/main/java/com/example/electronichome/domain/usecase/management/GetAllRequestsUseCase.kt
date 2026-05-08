package com.example.electronichome.domain.usecase.management

import com.example.electronichome.data.repository.ManagementRepository
import com.example.electronichome.domain.model.RequestResponse
import javax.inject.Inject

class GetAllRequestsUseCase @Inject constructor(
    private val repository: ManagementRepository
) {
    suspend operator fun invoke(): Result<List<RequestResponse>> =
        repository.getAllRequests()
}