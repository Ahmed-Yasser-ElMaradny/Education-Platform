package com.ahmed.ostazahmed.StudentRecyclerViewsAndRepo

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed.ostazahmed.Domain.Lesson
import kotlinx.coroutines.launch

class StudentViewModel : ViewModel() {
    private val studentRepository = StudentRepository()


    fun onCourseClicked(
        activity: Activity,
        lesson: Lesson,
        onSuccess: (String) -> Unit,
        onError: (Exception, String) -> Unit,
    ) {
        viewModelScope.launch {
            studentRepository.onCourseClicked(activity, lesson, onSuccess, onError)
        }
    }

    fun onRedeemClicked(
        code: String,
        onSuccess: (String) -> Unit,
        onError: (Exception, String) -> Unit,

        ) {
        viewModelScope.launch {
            studentRepository.onRedeemClicked(code, onSuccess, onError)
        }

    }

    fun date(time: String) {
        viewModelScope.launch {
            studentRepository.date(time)
        }
    }
}