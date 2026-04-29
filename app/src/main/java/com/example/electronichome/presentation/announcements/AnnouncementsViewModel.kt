package com.example.electronichome.presentation.announcements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electronichome.data.repository.AnnouncementRepository
import com.example.electronichome.domain.model.AnnouncementCategoryUi
import com.example.electronichome.domain.model.AnnouncementResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnnouncementsUiState(
    val announcements: List<AnnouncementResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: AnnouncementCategoryUi = AnnouncementCategoryUi.ALL
)

@HiltViewModel
class AnnouncementsViewModel @Inject constructor(
    private val repository: AnnouncementRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AnnouncementsUiState())
    val state: StateFlow<AnnouncementsUiState> = _state.asStateFlow()

    init { load() }

    fun selectCategory(cat: AnnouncementCategoryUi) {
        _state.value = _state.value.copy(selectedCategory = cat)
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val categoryKey = _state.value.selectedCategory
                .takeIf { it != AnnouncementCategoryUi.ALL }?.key
            repository.getAnnouncements(categoryKey)
                .onSuccess { _state.value = _state.value.copy(announcements = it, isLoading = false) }
                .onFailure { _state.value = _state.value.copy(isLoading = false, error = it.message) }
        }
    }
}