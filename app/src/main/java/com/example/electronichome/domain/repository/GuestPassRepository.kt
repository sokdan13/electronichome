package com.example.electronichome.domain.repository

import com.example.electronichome.domain.model.GuestPassCreateDto
import com.example.electronichome.domain.model.GuestPassResponse

interface GuestPassRepository {

    suspend fun createPass(
        dto: GuestPassCreateDto
    ): Result<GuestPassResponse>

    suspend fun getMyPasses(): Result<List<GuestPassResponse>>

    suspend fun cancelPass(
        token: String
    ): Result<Unit>
}