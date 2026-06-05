package com.example.electronichome.domain.repository

import com.example.electronichome.domain.model.RequestCreateDto
import com.example.electronichome.domain.model.RequestResponse

interface RequestRepository {

    suspend fun createRequest(
        dto: RequestCreateDto
    ): Result<RequestResponse>

    suspend fun getMyRequests(): Result<List<RequestResponse>>
}