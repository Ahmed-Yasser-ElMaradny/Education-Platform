package com.ahmed.ostazMohamed.TeacherFragments.TeacherCourses

import com.ahmed.ostazMohamed.Domain.Lesson
import com.ahmed.ostazMohamed.Utils.FirebaseManager
import com.google.firebase.firestore.ListenerRegistration

class CoursesRepository {
    val fireBaseDatabase = FirebaseManager.db

    fun observeCourses(
        onSuccess: (List<Lesson>) -> Unit,
        onError: (Exception) -> Unit,
    ): ListenerRegistration {
        return fireBaseDatabase.collection("lesson").addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val coursesList = mutableListOf<Lesson>()

                for (document in snapshot.documents) {
                    val lesson = document.toObject(Lesson::class.java)
                    if (lesson != null) {
                        coursesList.add(lesson)
                    }
                }
                onSuccess(coursesList)
            }

        }
    }
}