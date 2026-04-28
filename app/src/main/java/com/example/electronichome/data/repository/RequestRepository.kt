package com.example.electronichome.data.repository

import com.example.electronichome.di.ApiClient
import com.example.electronichome.domain.model.ApiResponse
import com.example.electronichome.domain.model.RequestCreateDto
import com.example.electronichome.domain.model.RequestResponse
import com.google.firebase.auth.FirebaseAuth
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestRepository @Inject constructor() {

    private suspend fun token() = FirebaseAuth.getInstance()
        .currentUser?.getIdToken(false)?.await()?.token
        ?: throw Exception("Не авторизован")

    suspend fun createRequest(dto: RequestCreateDto): Result<RequestResponse> = runCatching {
        val resp: ApiResponse<RequestResponse> = ApiClient.client
            .post("${ApiClient.BASE_URL}/requests") {
                bearerAuth(token())
                contentType(ContentType.Application.Json)
                setBody(dto)
            }.body()
        resp.data ?: throw Exception(resp.error)
    }

    suspend fun getMyRequests(): Result<List<RequestResponse>> = runCatching {
        val resp: ApiResponse<List<RequestResponse>> = ApiClient.client
            .get("${ApiClient.BASE_URL}/requests/my") {
                bearerAuth(token())
            }.body()
        resp.data ?: emptyList()
    }
}