package com.example.electronichome.presentation.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electronichome.data.repository.RequestRepository
import com.example.electronichome.domain.model.RequestCreateDto
import com.example.electronichome.domain.model.RequestResponse
import com.example.electronichome.domain.usecase.request.GetMyRequestsUseCase
import com.example.electronichome.domain.usecase.request.SubmitRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RequestsUiState(
    val requests: List<RequestResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isSubmitSuccess: Boolean = false,
    val error: String? = null,
    val isConnectionError: Boolean = false
)

@HiltViewModel
class RequestsViewModel @Inject constructor(
    private val getMyRequests: GetMyRequestsUseCase, private val createRequest: SubmitRequestUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RequestsUiState())
    val state: StateFlow<RequestsUiState> = _state.asStateFlow()

    init {
        loadRequests()
    }

    fun loadRequests() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            getMyRequests().onSuccess {
                _state.value = _state.value.copy(requests = it, isLoading = false)
            }.onFailure { e ->

                val isConnectionProblem = e is java.io.IOException

                _state.value = _state.value.copy(
                    isLoading = false, error = e.message, isConnectionError = isConnectionProblem
                )
            }
        }
    }

    fun submitRequest(dto: RequestCreateDto) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            createRequest(dto).onSuccess {
                loadRequests()
                _state.value = _state.value.copy(isLoading = false, isSubmitSuccess = true)
            }.onFailure {
                _state.value = _state.value.copy(isLoading = false, error = it.message)
            }
        }
    }

    fun resetSuccess() {
        _state.value = _state.value.copy(isSubmitSuccess = false)
    }
}