package com.example.electronichome.domain.repository

import com.example.electronichome.domain.model.ApartmentResponse
import com.example.electronichome.domain.model.ApproveRequest
import com.example.electronichome.domain.model.RequestResponse

interface ManagementRepository {

    suspend fun getPendingApartments(): Result<List<ApartmentResponse>>

    suspend fun getAllApartments(): Result<List<ApartmentResponse>>

    suspend fun approveApartment(
        id: String,
        req: ApproveRequest
    ): Result<ApartmentResponse>

    suspend fun rejectApartment(
        id: String,
        note: String
    ): Result<ApartmentResponse>

    suspend fun getAllRequests(): Result<List<RequestResponse>>

    suspend fun takeRequestInProgress(
        id: String,
        dueDate: String
    ): Result<RequestResponse>

    suspend fun markRequestDone(
        id: String
    ): Result<RequestResponse>

    suspend fun rejectRequest(
        id: String
    ): Result<RequestResponse>
}