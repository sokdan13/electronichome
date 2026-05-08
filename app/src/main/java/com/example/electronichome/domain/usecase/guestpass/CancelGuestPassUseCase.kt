package com.example.electronichome.domain.usecase.guestpass

import com.example.electronichome.data.repository.GuestPassRepository
import javax.inject.Inject

class CancelGuestPassUseCase @Inject constructor(
    private val repository: GuestPassRepository
) {
    suspend operator fun invoke(token: String): Result<Unit> {
        if (token.isBlank())
            return Result.failure(Exception("Токен пропуска не найден"))

        return repository.cancelPass(token)
    }
}