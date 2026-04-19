package com.example.electronichome.data.local

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApartmentPrefs @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("apartment_prefs", Context.MODE_PRIVATE)

    var primaryApartmentId: String?
        get() = prefs.getString("primary_id", null)
        set(value) = prefs.edit { putString("primary_id", value) }
}