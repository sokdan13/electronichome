package com.example.electronichome.domain.usecase.apartment

import com.example.electronichome.data.local.ApartmentPrefs
import javax.inject.Inject

class SetPrimaryApartmentUseCase @Inject constructor(
    private val prefs: ApartmentPrefs
) {
    operator fun invoke(id: String) {
        if (id.isBlank()) return
        prefs.primaryApartmentId = id
    }
}