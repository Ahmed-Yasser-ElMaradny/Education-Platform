package com.ahmed.ostazahmed.AuthenticationHelper

import AuthRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    fun signIn(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            authRepository.signIn(email, password, onResult)
        }
    }

    fun signUp(
        displayName: String,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit,
    ) {
        viewModelScope.launch {
            authRepository.signUp(displayName, email, password, onResult)
        }
    }

    fun signInWithGoogle(idToken: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch { authRepository.signInWithGoogle(idToken, onResult) }
    }

    fun getUserRole(userId: String, onResult: (String?, Boolean) -> Unit) {
        viewModelScope.launch {
            authRepository.getUserRole(userId, onResult)

        }
    }

    fun forgotPassword(email: String, onResult: (Boolean, String) -> Unit) {
        // هنا ممكن تحط حالة الـ Loading عشان تظهر Spinner في الشاشة
        authRepository.resetPassword(email) { isSuccess, message ->
            if (isSuccess) {
                onResult(true, message)
            } else {
                onResult(false, message)
            }
        }
    }
}
