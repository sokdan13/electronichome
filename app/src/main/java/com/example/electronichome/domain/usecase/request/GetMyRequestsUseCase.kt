package com.example.electronichome.domain.usecase.request

import com.example.electronichome.data.repository.RequestRepositoryImpl
import com.example.electronichome.domain.model.RequestResponse
import com.example.electronichome.domain.repository.RequestRepository
import javax.inject.Inject

class GetMyRequestsUseCase @Inject constructor(
    private val repository: RequestRepository
) {
    suspend operator fun invoke(): Result<List<RequestResponse>> =
        repository.getMyRequests()
}