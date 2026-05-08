package com.example.electronichome.domain.usecase.management

import com.example.electronichome.data.repository.ManagementRepository
import com.example.electronichome.domain.model.RequestResponse
import javax.inject.Inject

class TakeRequestInProgressUseCase @Inject constructor(
    private val repository: ManagementRepository
) {
    suspend operator fun invoke(id: String, dueDate: String): Result<RequestResponse> {
        if (id.isBlank())
            return Result.failure(Exception("ID заявки не найден"))
        if (!dueDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
            return Result.failure(Exception("Некорректный формат даты. Используйте ГГГГ-ММ-ДД"))

        return repository.takeRequestInProgress(id, dueDate)
    }
}