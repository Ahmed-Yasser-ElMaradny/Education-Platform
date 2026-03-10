package com.ahmed.ostazMohamed.TeacherFragments.generator

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed.ostazMohamed.Domain.AccessCode
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

class GenerateCodesViewModel : ViewModel() {

    private val _codes = MutableLiveData<List<AccessCode>>()
    val codes: LiveData<List<AccessCode>> = _codes
    private val fireBaseDatabase = Firebase.firestore
    private var listenerRegistration: ListenerRegistration? = null
    private val generatorRepository = GeneratorRepository()

    fun observeGeneratedCodes() {
        viewModelScope.launch {
            listenerRegistration =
                generatorRepository.observeGeneratedCodes(
                     onSuccess = { codesList ->
                    _codes.postValue(codesList) },
                     onError = { exception ->
                    _codes.postValue(emptyList())
                })

        }
    }

    fun deleteAllCodes() {
        viewModelScope.launch {
            generatorRepository.deleteAllCodes(
                onSuccess = { codesList ->
                    _codes.postValue(codesList) },
                onError = { exception ->
                    _codes.postValue(emptyList())
                })
        }
    }

   fun addCodes(accessCode: AccessCode) {
        viewModelScope.launch {
            generatorRepository.addCodes(accessCode)
            observeGeneratedCodes()
        }
    }
    @SuppressLint("SimpleDateFormat")
    fun codeValidation(){
        viewModelScope.launch {
                generatorRepository.codeValidation()
            }
        }


    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}