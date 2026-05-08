package com.example.electronichome.domain.usecase.meter

import com.example.electronichome.data.repository.MeterRepository
import com.example.electronichome.domain.model.MeterReadingRequest
import com.example.electronichome.domain.model.MeterReadingResponse
import javax.inject.Inject

class SubmitMeterReadingUseCase @Inject constructor(
    private val repository: MeterRepository
) {
    suspend operator fun invoke(request: MeterReadingRequest): Result<MeterReadingResponse> {
        if (request.apartmentId.isBlank())
            return Result.failure(Exception("ID квартиры не найден"))
        if (request.month !in 1..12)
            return Result.failure(Exception("Некорректный месяц"))
        if (request.year < 2000)
            return Result.failure(Exception("Некорректный год"))

        val allEmpty = listOf(
            request.hotWater, request.coldWater, request.heating,
            request.elecDay,  request.elecNight, request.elecPeak
        ).all { it == null }
        if (allEmpty)
            return Result.failure(Exception("Введите хотя бы одно показание"))

        return repository.submitReading(request)
    }
}