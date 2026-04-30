package com.example.electronichome.presentation.management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electronichome.data.repository.ManagementRepository
import com.example.electronichome.domain.model.ApproveRequest
import com.example.electronichome.domain.model.ApartmentResponse
import com.example.electronichome.domain.model.RequestResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManagementUiState(
    val apartments: List<ApartmentResponse> = emptyList(),
    val requests: List<RequestResponse>     = emptyList(),
    val isLoading: Boolean                  = false,
    val error: String?                      = null,
    val successMessage: String?             = null
)

@HiltViewModel
class ManagementViewModel @Inject constructor(
    private val repository: ManagementRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ManagementUiState())
    val state: StateFlow<ManagementUiState> = _state.asStateFlow()

    init {
        loadApartments()
        loadRequests()
    }

    fun loadApartments() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.getPendingApartments()
                .onSuccess { _state.value = _state.value.copy(apartments = it, isLoading = false) }
                .onFailure { _state.value = _state.value.copy(isLoading = false, error = it.message) }
        }
    }

    fun loadRequests() {
        viewModelScope.launch {
            repository.getAllRequests()
                .onSuccess { _state.value = _state.value.copy(requests = it) }
                .onFailure { _state.value = _state.value.copy(error = it.message) }
        }
    }

    fun approveApartment(id: String, req: ApproveRequest) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.approveApartment(id, req)
                .onSuccess {
                    loadApartments()
                    _state.value = _state.value.copy(successMessage = "Квартира подтверждена")
                }
                .onFailure { _state.value = _state.value.copy(isLoading = false, error = it.message) }
        }
    }

    fun rejectApartment(id: String, note: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.rejectApartment(id, note)
                .onSuccess {
                    loadApartments()
                    _state.value = _state.value.copy(successMessage = "Квартира отклонена")
                }
                .onFailure { _state.value = _state.value.copy(isLoading = false, error = it.message) }
        }
    }

    fun takeInProgress(id: String, dueDate: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.takeRequestInProgress(id, dueDate)
                .onSuccess {
                    loadRequests()
                    _state.value = _state.value.copy(
                        isLoading      = false,
                        successMessage = "Заявка взята в работу"
                    )
                }
                .onFailure { _state.value = _state.value.copy(isLoading = false, error = it.message) }
        }
    }

    fun markDone(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.markRequestDone(id)
                .onSuccess {
                    loadRequests()
                    _state.value = _state.value.copy(
                        isLoading      = false,
                        successMessage = "Заявка выполнена"
                    )
                }
                .onFailure { _state.value = _state.value.copy(isLoading = false, error = it.message) }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(error = null, successMessage = null)
    }
}