package com.valentinesgarage.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinesgarage.data.model.User
import com.valentinesgarage.data.model.UserRole
import com.valentinesgarage.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val selectedRole: UserRole = UserRole.MECHANIC,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val isAuthenticated: Boolean = false,
    val currentUser: User? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.value = _uiState.value.copy(
                    isAuthenticated = user != null,
                    currentUser = user
                )
            }
        }
    }

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(email = email, error = null)
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
    }

    fun onRoleChanged(role: UserRole) {
        _uiState.value = _uiState.value.copy(selectedRole = role)
    }

    fun login() {
        val email = _uiState.value.email
        val password = _uiState.value.password

        if (!isValidEmail(email)) {
            _uiState.value = _uiState.value.copy(error = "Invalid email")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val success = authRepository.login(email, password)
            if (success) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = null)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Login failed")
            }
        }
    }

    fun signUp() {
        val email = _uiState.value.email
        val password = _uiState.value.password
        val role = _uiState.value.selectedRole

        if (!isValidEmail(email)) {
            _uiState.value = _uiState.value.copy(error = "Invalid email")
            return
        }
        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(error = "Password too short")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val success = authRepository.signUp(email, password, role)
            if (success) {
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true, error = null)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Sign up failed (email exists?)")
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
