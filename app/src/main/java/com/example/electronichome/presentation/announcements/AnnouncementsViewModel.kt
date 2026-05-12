package com.example.electronichome.presentation.announcements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electronichome.domain.model.AnnouncementCategoryUi
import com.example.electronichome.domain.model.AnnouncementResponse
import com.example.electronichome.domain.usecase.announcement.GetAnnouncementsUseCase
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
    private val getAnnouncements: GetAnnouncementsUseCase,
) : ViewModel() {

    private var allAnnouncements: List<AnnouncementResponse> = emptyList()
    private val _state = MutableStateFlow(AnnouncementsUiState())
    val state: StateFlow<AnnouncementsUiState> = _state.asStateFlow()

    init { load() }

    fun selectCategory(cat: AnnouncementCategoryUi) {
        _state.value = _state.value.copy(
            selectedCategory = cat,
            announcements    = applyFilter(cat)
        )
    }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            getAnnouncements(null)
                .onSuccess { list ->
                    allAnnouncements = list
                    _state.value = _state.value.copy(
                        isLoading     = false,
                        announcements = applyFilter(_state.value.selectedCategory)
                    )
                }
                .onFailure {
                    _state.value = _state.value.copy(isLoading = false, error = it.message)
                }
        }
    }

    private fun applyFilter(category: AnnouncementCategoryUi): List<AnnouncementResponse> =
        if (category == AnnouncementCategoryUi.ALL) allAnnouncements
        else allAnnouncements.filter { it.category == category.key }
}