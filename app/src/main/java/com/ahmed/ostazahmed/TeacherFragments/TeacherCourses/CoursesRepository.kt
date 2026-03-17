package com.ahmed.ostazahmed.TeacherFragments.TeacherCourses

import com.ahmed.ostazahmed.Domain.Lesson
import com.ahmed.ostazahmed.Utils.FirebaseManager
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class CoursesRepository {
    val fireBaseDatabase = FirebaseManager.db

    fun observeCourses(
        academicYearFilter: Int, // الرقم اللي جاي من الشاشة
        onSuccess: (List<Lesson>) -> Unit,
        onError: (Exception) -> Unit,
    ): ListenerRegistration {

        // 1. بنعرف المتغير كـ Query عشان نقدر نعدل عليه براحتنا
        var query: Query = fireBaseDatabase.collection("lesson")

        // 2. الشرط الذكي: لو الرقم مش 0 (يعني اختار سنة معينة)، هنركب الفلتر في الـ Query
        if (academicYearFilter != 0) {
            query = query.whereEqualTo("academicYear", academicYearFilter)
        }

        // 3. ننفذ الـ Query (لو كان 0 هينفذ الكوليكشن كله من غير فلتر)
        return query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                onError(error)
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