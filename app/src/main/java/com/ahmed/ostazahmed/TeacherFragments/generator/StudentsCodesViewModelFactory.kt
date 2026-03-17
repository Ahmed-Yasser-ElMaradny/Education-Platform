package com.ahmed.ostazahmed.TeacherFragments.generator

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ahmed.ostazahmed.Utils.PdfManager

class StudentsCodesViewModelFactory(private val context : Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentCodesToPdfViewModel::class.java)) {
            val pdfManager = PdfManager(context)
            return StudentCodesToPdfViewModel(pdfManager) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

