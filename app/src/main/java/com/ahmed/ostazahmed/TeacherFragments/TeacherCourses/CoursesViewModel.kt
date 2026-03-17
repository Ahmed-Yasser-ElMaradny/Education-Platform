package com.ahmed.ostazahmed.TeacherFragments.TeacherCourses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed.ostazahmed.Domain.Lesson
import com.ahmed.ostazahmed.Utils.FirebaseManager
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch

class CoursesViewModel : ViewModel() {

    private val fireBaseDatabase = FirebaseManager.db

    private val _courses = MutableLiveData<List<Lesson>>()
    val courses: LiveData<List<Lesson>> = _courses
    private var listenerRegistration: ListenerRegistration? = null
    private val coursesRepository = CoursesRepository()


    fun observeCourses(academicYearFilter: Int = 0) {
        listenerRegistration?.remove()

        viewModelScope.launch {
            listenerRegistration =
               coursesRepository.observeCourses(academicYearFilter,
                   onSuccess = { coursesList ->
                    _courses.postValue(coursesList) },
                    onError = { exception ->
                        _courses.postValue(emptyList())
                    })
            }
     }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

}




