package com.example.electronichome.presentation.meters

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electronichome.data.repository.MeterRepository
import com.example.electronichome.domain.model.MeterReadingRequest
import com.example.electronichome.domain.model.MeterReadingResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class MetersUiState @RequiresApi(Build.VERSION_CODES.O) constructor(
    val readings: List<MeterReadingResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isSubmitSuccess: Boolean = false,
    val error: String? = null,
    val selectedMonth: Int = LocalDate.now().monthValue,
    val selectedYear: Int = LocalDate.now().year,
    val hotWater: String = "",
    val coldWater: String = "",
    val heating: String = "",
    val elecDay: String = "",
    val elecNight: String = "",
    val elecPeak: String = "",
    val showArchive: Boolean = false
)

@HiltViewModel
class MetersViewModel @Inject constructor(
    private val repo: MeterRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MetersUiState())
    val state: StateFlow<MetersUiState> = _state.asStateFlow()

    fun loadReadings(apartmentId: String) {
        viewModelScope.launch {
            repo.getReadings(apartmentId)
                .onSuccess { _state.value = _state.value.copy(readings = it) }
        }
    }

    fun updateField(field: String, value: String) {
        if (!value.matches(Regex("^\\d{0,5}(\\.\\d{0,3})?\$"))) return
        _state.value = when (field) {
            "hotWater"  -> _state.value.copy(hotWater  = value)
            "coldWater" -> _state.value.copy(coldWater = value)
            "heating"   -> _state.value.copy(heating   = value)
            "elecDay"   -> _state.value.copy(elecDay   = value)
            "elecNight" -> _state.value.copy(elecNight = value)
            "elecPeak"  -> _state.value.copy(elecPeak  = value)
            else        -> _state.value
        }
    }

    fun setMonth(month: Int) { _state.value = _state.value.copy(selectedMonth = month) }
    fun setYear(year: Int)   { _state.value = _state.value.copy(selectedYear  = year) }
    fun toggleArchive()      { _state.value = _state.value.copy(showArchive   = !_state.value.showArchive) }

    fun fillFromPrevious() {
        val prev = _state.value.readings.firstOrNull {
            val prevMonth = if (_state.value.selectedMonth == 1) 12 else _state.value.selectedMonth - 1
            val prevYear  = if (_state.value.selectedMonth == 1) _state.value.selectedYear - 1 else _state.value.selectedYear
            it.month == prevMonth && it.year == prevYear
        } ?: return
        _state.value = _state.value.copy(
            hotWater  = prev.hotWater?.toString()  ?: "",
            coldWater = prev.coldWater?.toString() ?: "",
            heating   = prev.heating?.toString()   ?: "",
            elecDay   = prev.elecDay?.toString()   ?: "",
            elecNight = prev.elecNight?.toString() ?: "",
            elecPeak  = prev.elecPeak?.toString()  ?: ""
        )
    }

    fun submit(apartmentId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val req = MeterReadingRequest(
                apartmentId = apartmentId,
                month       = _state.value.selectedMonth,
                year        = _state.value.selectedYear,
                hotWater    = _state.value.hotWater.toDoubleOrNull(),
                coldWater   = _state.value.coldWater.toDoubleOrNull(),
                heating     = _state.value.heating.toDoubleOrNull(),
                elecDay     = _state.value.elecDay.toDoubleOrNull(),
                elecNight   = _state.value.elecNight.toDoubleOrNull(),
                elecPeak    = _state.value.elecPeak.toDoubleOrNull()
            )
            repo.submitReading(req)
                .onSuccess {
                    loadReadings(apartmentId)
                    _state.value = _state.value.copy(isLoading = false, isSubmitSuccess = true)
                }
                .onFailure {
                    _state.value = _state.value.copy(isLoading = false, error = it.message)
                }
        }
    }
}