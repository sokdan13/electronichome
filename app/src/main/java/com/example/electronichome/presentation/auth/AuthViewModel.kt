package com.example.electronichome.presentation.auth


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val error: String?     = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = AuthState(error = "Заполните все поля")
            return
        }
        viewModelScope.launch {
            _state.value = AuthState(isLoading = true)
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _state.value = AuthState(isSuccess = true)
            } catch (e: Exception) {
                _state.value = AuthState(error = e.localizedMessage ?: "Ошибка входа")
            }
        }
    }

    fun register(email: String, password: String, firstName: String, lastName: String) {
        if (email.isBlank() || password.isBlank() || firstName.isBlank() || lastName.isBlank()) {
            _state.value = AuthState(error = "Заполните все поля")
            return
        }
        if (password.length < 6) {
            _state.value = AuthState(error = "Пароль минимум 6 символов")
            return
        }
        viewModelScope.launch {
            _state.value = AuthState(isLoading = true)
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                // Сохраняем displayName в Firebase профиле
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName("$firstName $lastName")
                    .build()
                result.user?.updateProfile(profileUpdate)?.await()
                _state.value = AuthState(isSuccess = true)
            } catch (e: Exception) {
                _state.value = AuthState(error = e.localizedMessage ?: "Ошибка регистрации")
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}