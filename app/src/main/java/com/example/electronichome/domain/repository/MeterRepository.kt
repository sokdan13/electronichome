package com.example.electronichome.domain.repository

import com.example.electronichome.domain.model.MeterReadingRequest
import com.example.electronichome.domain.model.MeterReadingResponse

interface MeterRepository {

    suspend fun submitReading(
        req: MeterReadingRequest
    ): Result<MeterReadingResponse>

    suspend fun getReadings(
        apartmentId: String
    ): Result<List<MeterReadingResponse>>
}