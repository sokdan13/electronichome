package com.example.electronichome.domain.repository

import com.example.electronichome.domain.model.ApartmentRequest
import com.example.electronichome.domain.model.ApartmentResponse

interface ApartmentRepository {

    suspend fun getMyApartments(): Result<List<ApartmentResponse>>

    suspend fun addApartment(
        request: ApartmentRequest
    ): Result<ApartmentResponse>
}