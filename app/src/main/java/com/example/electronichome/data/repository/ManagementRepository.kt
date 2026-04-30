package com.example.electronichome.data.repository

import com.example.electronichome.di.ApiClient
import com.example.electronichome.domain.model.ApiResponse
import com.example.electronichome.domain.model.ApproveRequest
import com.example.electronichome.domain.model.ApartmentResponse
import com.example.electronichome.domain.model.RejectRequest
import com.example.electronichome.domain.model.RequestResponse
import com.example.electronichome.domain.model.TakeInProgressDto
import com.google.firebase.auth.FirebaseAuth
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManagementRepository @Inject constructor() {

    private suspend fun token() = FirebaseAuth.getInstance()
        .currentUser?.getIdToken(false)?.await()?.token
        ?: throw Exception("Не авторизован")

    suspend fun getPendingApartments(): Result<List<ApartmentResponse>> = runCatching {
        val resp: ApiResponse<List<ApartmentResponse>> = ApiClient.client
            .get("${ApiClient.BASE_URL}/management/apartments?status=PENDING") {
                bearerAuth(token())
            }.body()
        resp.data ?: emptyList()
    }

    suspend fun getAllApartments(): Result<List<ApartmentResponse>> = runCatching {
        val resp: ApiResponse<List<ApartmentResponse>> = ApiClient.client
            .get("${ApiClient.BASE_URL}/management/apartments") {
                bearerAuth(token())
            }.body()
        resp.data ?: emptyList()
    }

    suspend fun approveApartment(
        id: String,
        req: ApproveRequest
    ): Result<ApartmentResponse> = runCatching {
        val resp: ApiResponse<ApartmentResponse> = ApiClient.client
            .patch("${ApiClient.BASE_URL}/management/apartments/$id/approve") {
                bearerAuth(token())
                contentType(ContentType.Application.Json)
                setBody(req)
            }.body()
        resp.data ?: throw Exception(resp.error)
    }

    suspend fun rejectApartment(id: String, note: String): Result<ApartmentResponse> = runCatching {
        val resp: ApiResponse<ApartmentResponse> = ApiClient.client
            .patch("${ApiClient.BASE_URL}/management/apartments/$id/reject") {
                bearerAuth(token())
                contentType(ContentType.Application.Json)
                setBody(RejectRequest(note))
            }.body()
        resp.data ?: throw Exception(resp.error)
    }

    suspend fun getAllRequests(): Result<List<RequestResponse>> = runCatching {
        val resp: ApiResponse<List<RequestResponse>> = ApiClient.client
            .get("${ApiClient.BASE_URL}/management/requests") {
                bearerAuth(token())
            }.body()
        resp.data ?: emptyList()
    }

    suspend fun takeRequestInProgress(
        id: String,
        dueDate: String
    ): Result<RequestResponse> = runCatching {
        val resp: ApiResponse<RequestResponse> = ApiClient.client
            .patch("${ApiClient.BASE_URL}/management/requests/$id/in-progress") {
                bearerAuth(token())
                contentType(ContentType.Application.Json)
                setBody(TakeInProgressDto(dueDate))
            }.body()
        resp.data ?: throw Exception(resp.error)
    }

    suspend fun markRequestDone(id: String): Result<RequestResponse> = runCatching {
        val resp: ApiResponse<RequestResponse> = ApiClient.client
            .patch("${ApiClient.BASE_URL}/management/requests/$id/done") {
                bearerAuth(token())
            }.body()
        resp.data ?: throw Exception(resp.error)
    }
}

