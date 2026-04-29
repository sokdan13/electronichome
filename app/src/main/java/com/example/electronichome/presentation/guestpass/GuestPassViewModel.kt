package com.example.electronichome.presentation.guestpass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electronichome.data.repository.GuestPassRepository
import com.example.electronichome.domain.model.GuestPassCreateDto
import com.example.electronichome.domain.model.GuestPassResponse
import com.example.electronichome.domain.model.PassDuration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GuestPassUiState(
    val passes: List<GuestPassResponse> = emptyList(),
    val activePass: GuestPassResponse?  = null,
    val isLoading: Boolean              = false,
    val error: String?                  = null,
    val selectedDuration: PassDuration  = PassDuration.THIRTY,
    val secondsLeft: Long               = 0
)

@HiltViewModel
class GuestPassViewModel @Inject constructor(
    private val repository: GuestPassRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GuestPassUiState())
    val state: StateFlow<GuestPassUiState> = _state.asStateFlow()

    private var countdownJob: Job? = null

    init { loadPasses() }

    fun loadPasses() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.getMyPasses()
                .onSuccess { passes ->
                    val active = passes.firstOrNull { it.isValid }
                    _state.value = _state.value.copy(
                        passes     = passes,
                        activePass = active,
                        isLoading  = false
                    )
                    active?.let { startCountdown(it.minutesLeft * 60) }
                }
                .onFailure {
                    _state.value = _state.value.copy(isLoading = false, error = it.message)
                }
        }
    }

    fun selectDuration(duration: PassDuration) {
        _state.value = _state.value.copy(selectedDuration = duration)
    }

    fun createPass(apartmentId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            repository.createPass(
                GuestPassCreateDto(
                    apartmentId     = apartmentId,
                    durationMinutes = _state.value.selectedDuration.minutes
                )
            )
                .onSuccess { pass ->
                    _state.value = _state.value.copy(
                        activePass = pass,
                        isLoading  = false
                    )
                    startCountdown(pass.minutesLeft * 60)
                    loadPasses()
                }
                .onFailure {
                    _state.value = _state.value.copy(isLoading = false, error = it.message)
                }
        }
    }

    private fun startCountdown(seconds: Long) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            var remaining = seconds
            while (remaining > 0) {
                _state.value = _state.value.copy(secondsLeft = remaining)
                delay(1_000)
                remaining--
            }
            _state.value = _state.value.copy(
                secondsLeft = 0,
                activePass  = null
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}