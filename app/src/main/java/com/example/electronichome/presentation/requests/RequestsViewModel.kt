package com.example.electronichome.presentation.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electronichome.data.repository.RequestRepository
import com.example.electronichome.domain.model.RequestCreateDto
import com.example.electronichome.domain.model.RequestResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RequestsUiState(
    val requests: List<RequestResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isSubmitSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class RequestsViewModel @Inject constructor(
    private val repository: RequestRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RequestsUiState())
    val state: StateFlow<RequestsUiState> = _state.asStateFlow()

    init { loadRequests() }

    fun loadRequests() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            repository.getMyRequests()
                .onSuccess { _state.value = _state.value.copy(requests = it, isLoading = false) }
                .onFailure { _state.value = _state.value.copy(isLoading = false, error = it.message) }
        }
    }

    fun submitRequest(dto: RequestCreateDto) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            repository.createRequest(dto)
                .onSuccess {
                    loadRequests()
                    _state.value = _state.value.copy(isLoading = false, isSubmitSuccess = true)
                }
                .onFailure { _state.value = _state.value.copy(isLoading = false, error = it.message) }
        }
    }

    fun resetSuccess() { _state.value = _state.value.copy(isSubmitSuccess = false) }
}