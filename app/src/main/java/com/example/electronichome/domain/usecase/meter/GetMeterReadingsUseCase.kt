package com.example.electronichome.domain.usecase.meter

import com.example.electronichome.data.repository.MeterRepositoryImpl
import com.example.electronichome.domain.model.MeterReadingResponse
import com.example.electronichome.domain.repository.MeterRepository
import javax.inject.Inject

class GetMeterReadingsUseCase @Inject constructor(
    private val repository: MeterRepository
) {
    suspend operator fun invoke(apartmentId: String): Result<List<MeterReadingResponse>> {
        if (apartmentId.isBlank())
            return Result.failure(Exception("ID квартиры не найден"))

        return repository.getReadings(apartmentId)
    }
}