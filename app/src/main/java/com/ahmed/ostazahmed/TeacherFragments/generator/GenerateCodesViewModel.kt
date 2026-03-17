package com.ahmed.ostazahmed.TeacherFragments.generator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed.ostazahmed.Domain.AccessCode
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GenerateCodesViewModel : ViewModel() {

    private val _codes = MutableStateFlow<List<AccessCode>>(emptyList())
    val codes = _codes.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null
    private val generatorRepository = GeneratorRepository()

    init {
        observeGeneratedCodes()
    }

    fun observeGeneratedCodes() {
        listenerRegistration = generatorRepository.observeGeneratedCodes(
            onSuccess = { codesList ->
                // الداتا دلوقتي جاية مترتبة وجاهزة من الفايربيز
                _codes.value = codesList
            },
            onError = { exception ->
                _codes.value = emptyList()
            }
        )
    }

    fun deleteAllCodes() {
        viewModelScope.launch {
            generatorRepository.deleteAllCodes()
        }
    }

    fun addCodes(accessCode: AccessCode) {
        viewModelScope.launch {
            generatorRepository.addCodes(accessCode)
        }
    }

    fun codeValidation() {
        viewModelScope.launch {
            // الـ Repository هو اللي بيتكفل بنقلها للـ Background دلوقتي
            generatorRepository.codeValidation()
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}