package com.ahmed.ostazahmed.TeacherFragments.studentsManagement

import com.ahmed.ostazahmed.Domain.User
import com.ahmed.ostazahmed.Utils.Collections
import com.ahmed.ostazahmed.Utils.FirebaseManager
import com.google.firebase.firestore.ListenerRegistration

class StudentManagementRepository {

    val fireBaseDatabase = FirebaseManager.db


    fun observeStudents(onSuccess: (List<User>) -> Unit, onError: (Exception) -> Unit): ListenerRegistration {
       return fireBaseDatabase.collection("users").addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val studentsList = mutableListOf<User>()
                for (document in snapshot.documents) {
                    val student = document.toObject(User::class.java)
                    if (student != null && student.role != "Teacher") {
                        studentsList.add(student)
                    }
                    onSuccess(studentsList)
                }
            }
        }
    }
    fun blockStudent(user: User, onSuccess: (String) -> Unit, onError: (Exception , String) -> Unit) {
        fireBaseDatabase.collection(Collections.USERS_COLLECTION).document(user.uid.toString())
            .update("blocked", true) // عدل دي بس
            .addOnSuccessListener {
                onSuccess("Blocked ${user.displayName}")
            }
            .addOnFailureListener {
                onError(it , "Failed to block")
            }
    }

    fun recoverStudent(user: User, onSuccess: (String) -> Unit, onError: (Exception , String) -> Unit) {
        fireBaseDatabase.collection("users").document(user.uid.toString())
            .update("blocked", false) // عدل دي بس
            .addOnSuccessListener {
                onSuccess("Recovered ${user.displayName}")
            }
            .addOnFailureListener {
                onError(it , "Failed to recover")
            }
    }
}