package com.example.electronichome.data.repository


import com.example.electronichome.di.ApiClient
import com.example.electronichome.domain.model.ApiResponse
import com.example.electronichome.domain.model.GuestPassCreateDto
import com.example.electronichome.domain.model.GuestPassResponse
import com.google.firebase.auth.FirebaseAuth
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GuestPassRepository @Inject constructor() {

    private suspend fun token() = FirebaseAuth.getInstance()
        .currentUser?.getIdToken(false)?.await()?.token
        ?: throw Exception("Не авторизован")

    suspend fun createPass(dto: GuestPassCreateDto): Result<GuestPassResponse> = runCatching {
        val resp: ApiResponse<GuestPassResponse> = ApiClient.client
            .post("${ApiClient.BASE_URL}/guest-passes") {
                bearerAuth(token())
                contentType(ContentType.Application.Json)
                setBody(dto)
            }.body()
        resp.data ?: throw Exception(resp.error)
    }

    suspend fun getMyPasses(): Result<List<GuestPassResponse>> = runCatching {
        val resp: ApiResponse<List<GuestPassResponse>> = ApiClient.client
            .get("${ApiClient.BASE_URL}/guest-passes/my") {
                bearerAuth(token())
            }.body()
        resp.data ?: emptyList()
    }
}