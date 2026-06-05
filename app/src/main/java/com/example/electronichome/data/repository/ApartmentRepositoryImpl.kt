package com.example.electronichome.data.repository

import com.example.electronichome.di.ApiClient
import com.example.electronichome.domain.model.ApiResponse
import com.example.electronichome.domain.model.ApartmentRequest
import com.example.electronichome.domain.model.ApartmentResponse
import com.example.electronichome.domain.repository.ApartmentRepository
import com.google.firebase.auth.FirebaseAuth
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class ApartmentRepositoryImpl @Inject constructor() : ApartmentRepository {

    private suspend fun getToken(): String {
        return FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.await()?.token
            ?: throw Exception("Пользователь не авторизован")
    }

    override suspend fun getMyApartments(): Result<List<ApartmentResponse>> = runCatching {
        val token = getToken()
        val response: ApiResponse<List<ApartmentResponse>> =
            ApiClient.client.get("${ApiClient.BASE_URL}/apartments/my") {
                bearerAuth(token)
            }.body()
        response.data ?: emptyList()
    }

    override suspend fun addApartment(request: ApartmentRequest): Result<ApartmentResponse> = runCatching {
        val token = getToken()
        val response: ApiResponse<ApartmentResponse> =
            ApiClient.client.post("${ApiClient.BASE_URL}/apartments") {
                bearerAuth(token)
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
        response.data ?: throw Exception(response.error ?: "Ошибка")
    }
}