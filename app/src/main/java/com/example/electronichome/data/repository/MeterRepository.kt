package com.example.electronichome.data.repository

import com.example.electronichome.di.ApiClient
import com.example.electronichome.domain.model.ApiResponse
import com.example.electronichome.domain.model.MeterReadingRequest
import com.example.electronichome.domain.model.MeterReadingResponse
import com.google.firebase.auth.FirebaseAuth
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MeterRepository @Inject constructor() {

    private suspend fun token() = FirebaseAuth.getInstance()
        .currentUser?.getIdToken(false)?.await()?.token
        ?: throw Exception("Не авторизован")

    suspend fun submitReading(req: MeterReadingRequest): Result<MeterReadingResponse> = runCatching {
        val resp: ApiResponse<MeterReadingResponse> = ApiClient.client
            .post("${ApiClient.BASE_URL}/meters") {
                bearerAuth(token())
                contentType(ContentType.Application.Json)
                setBody(req)
            }.body()
        resp.data ?: throw Exception(resp.error)
    }

    suspend fun getReadings(apartmentId: String): Result<List<MeterReadingResponse>> = runCatching {
        val resp: ApiResponse<List<MeterReadingResponse>> = ApiClient.client
            .get("${ApiClient.BASE_URL}/meters/$apartmentId") {
                bearerAuth(token())
            }.body()
        resp.data ?: emptyList()
    }
}