package com.example.electronichome.domain.usecase.request

import com.example.electronichome.data.repository.RequestRepository
import com.example.electronichome.domain.model.RequestResponse
import javax.inject.Inject

class GetMyRequestsUseCase @Inject constructor(
    private val repository: RequestRepository
) {
    suspend operator fun invoke(): Result<List<RequestResponse>> =
        repository.getMyRequests()
}