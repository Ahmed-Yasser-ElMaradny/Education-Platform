package com.ahmed.ostazMohamed.TeacherFragments.studentsManagement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed.ostazMohamed.Domain.User
import com.ahmed.ostazMohamed.Utils.FirebaseManager
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch

class StudentsManagementsViewModel : ViewModel() {

    private val _students = MutableLiveData<List<User>>()
    val students: LiveData<List<User>> = _students
    val fireBaseDatabase = FirebaseManager.db
    private var listenerRegistration: ListenerRegistration? = null
    private val studentsRepository = StudentManagementRepository()

    fun observeStudents() {
        viewModelScope.launch {
            listenerRegistration =
                studentsRepository.observeStudents(
                    onSuccess = { studentsList ->
                        _students.postValue(studentsList)
                    },
                    onError = { exception ->
                        _students.postValue(emptyList())
                    })

        }
    }

    fun blockStudent(user: User , onSuccess: (String) -> Unit, onError: (Exception , String) -> Unit) {
        viewModelScope.launch {
            studentsRepository.blockStudent(user,
                onSuccess = { task ->
                    onSuccess(task)
            },  onError = { exception, task ->
                    onError(exception , task)
            })
        }
    }

    fun recoverStudent(user: User , onSuccess: (String) -> Unit, onError: (Exception , String) -> Unit) {
        viewModelScope.launch {
            studentsRepository.recoverStudent(user,
                onSuccess = { task ->
                    onSuccess(task)},
                onError = { exception, task ->
                    onError(exception , task)
                })
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

}