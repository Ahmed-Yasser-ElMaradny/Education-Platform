package com.ahmed.ostazahmed.TeacherFragments.generator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed.ostazahmed.Utils.PdfManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StudentCodesToPdfViewModel(private val pdfManager: PdfManager) : ViewModel() {

    private val _pdfGenerationStatus = MutableStateFlow<Boolean?>(null)
    val pdfGenerationStatus: StateFlow<Boolean?> = _pdfGenerationStatus

    fun generatePdfForStudents(codesList: List<String>) {
        viewModelScope.launch {
            val isSuccess = pdfManager.createCodesPdf(codesList)
            _pdfGenerationStatus.value = isSuccess
        }
    }
}