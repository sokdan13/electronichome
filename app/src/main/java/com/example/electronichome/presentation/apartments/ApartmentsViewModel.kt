package com.example.electronichome.presentation.apartments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electronichome.data.local.ApartmentPrefs
import com.example.electronichome.data.repository.ApartmentRepository
import com.example.electronichome.domain.model.ApartmentRequest
import com.example.electronichome.domain.model.ApartmentResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ApartmentsUiState(
    val apartments: List<ApartmentResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAddSuccess: Boolean = false
)

@HiltViewModel
class ApartmentsViewModel @Inject constructor(
    private val repository: ApartmentRepository,
    private val prefs: ApartmentPrefs
) : ViewModel() {

    private val _state = MutableStateFlow(ApartmentsUiState())
    val state: StateFlow<ApartmentsUiState> = _state.asStateFlow()

    private val _primaryId = MutableStateFlow(prefs.primaryApartmentId)
    val primaryId: StateFlow<String?> = _primaryId.asStateFlow()

    init { loadApartments() }

    fun loadApartments() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.getMyApartments()
                .onSuccess { list ->
                    if (_primaryId.value == null) {
                        list.firstOrNull { it.status == "APPROVED" }?.let {
                            setPrimary(it.id)
                        }
                    }
                    _state.value = _state.value.copy(apartments = list, isLoading = false)
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(isLoading = false, error = e.message)
                }
        }
    }

    fun setPrimary(id: String) {
        prefs.primaryApartmentId = id
        _primaryId.value = id
    }

    fun getPrimaryApartment() = state.value.apartments.firstOrNull {
        it.id == _primaryId.value
    }

    fun addApartment(request: ApartmentRequest) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            repository.addApartment(request)
                .onSuccess {
                    _state.value = _state.value.copy(isLoading = false, isAddSuccess = true)
                    loadApartments()
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(isLoading = false, error = e.message)
                }
        }
    }
}