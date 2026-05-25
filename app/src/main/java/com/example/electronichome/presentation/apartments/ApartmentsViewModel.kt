package com.example.electronichome.presentation.apartments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electronichome.data.local.ApartmentPrefs
import com.example.electronichome.domain.model.ApartmentRequest
import com.example.electronichome.domain.model.ApartmentResponse
import com.example.electronichome.domain.usecase.apartment.AddApartmentUseCase
import com.example.electronichome.domain.usecase.apartment.GetMyApartmentsUseCase
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
    val isAddSuccess: Boolean = false,
    val isConnectionError: Boolean = false
)

@HiltViewModel
class ApartmentsViewModel @Inject constructor(
    private val getMyApartments: GetMyApartmentsUseCase,
    private val addApartmentUseCase: AddApartmentUseCase,
    private val prefs: ApartmentPrefs
) : ViewModel() {

    private val _state = MutableStateFlow(ApartmentsUiState())
    val state: StateFlow<ApartmentsUiState> = _state.asStateFlow()

    private val _primaryId = MutableStateFlow(prefs.primaryApartmentId)
    val primaryId: StateFlow<String?> = _primaryId.asStateFlow()

    private val _primaryApartment = MutableStateFlow<ApartmentResponse?>(null)
    val primaryApartment = _primaryApartment.asStateFlow()

    init { loadApartments() }

    fun loadApartments() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            getMyApartments()
                .onSuccess { list ->

                    var currentPrimaryId = _primaryId.value

                    if (currentPrimaryId == null) {
                        currentPrimaryId =
                            list.firstOrNull { it.status == "APPROVED" }?.id

                        currentPrimaryId?.let {
                            prefs.primaryApartmentId = it
                            _primaryId.value = it
                        }
                    }

                    _primaryApartment.value =
                        list.firstOrNull { it.id == currentPrimaryId }

                    _state.value = _state.value.copy(
                        apartments = list,
                        isLoading = false,
                        isConnectionError = false
                    )

                }
                .onFailure { e ->

                    val isConnectionProblem =
                        e is java.io.IOException

                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message,
                        isConnectionError = isConnectionProblem
                    )
                }
        }
    }

    fun setPrimary(id: String) {
        prefs.primaryApartmentId = id
        _primaryId.value = id

        _primaryApartment.value =
            _state.value.apartments.firstOrNull { it.id == id }
    }

    fun getPrimaryApartment() = state.value.apartments.firstOrNull {
        it.id == _primaryId.value
    }

    fun addApartment(request: ApartmentRequest) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            addApartmentUseCase(request)
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