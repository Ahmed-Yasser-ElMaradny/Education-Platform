package com.ahmed.ostazMohamed

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ahmed.ostazMohamed.Domain.Lesson
import com.ahmed.ostazMohamed.databinding.ActivityAddCourseBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class AddCourseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddCourseBinding
    private val firebaseDatabase = Firebase.firestore
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCourseBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)


        binding.btnSaveLesson.setOnClickListener {

            val title = binding.etLessonName.text.toString()
            val description = binding.etDescription.text.toString()
            val creditsText = binding.etCredits.text.toString().trim()
            val videoId = extractVideoIdFromSharingLink(binding.etVideoId.text.toString().trim()).toString()


            val errorMessage = when {
                title.isEmpty() ->
                    "من فضلك اكتب اسم الدرس"

                description.isEmpty() ->
                    "من فضلك اكتب الوصف"

                creditsText.isEmpty() ->
                    "من فضلك اكتب عدد الكريدتس"

                creditsText.toIntOrNull() == null ->
                    "الكريدتس لازم تكون أرقام فقط"

                creditsText.toInt() < 1 ->
                    "أقل عدد كريدتس مسموح هو 1"

                videoId.isEmpty() ->
                    "من فضلك اكتب Video ID"

                else -> null
            }

            // لو في Error وقف التنفيذ
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // =========================
            // DATA VALID ✔
            // =========================

            val credits = creditsText.toInt()
            val lessonId = firebaseDatabase.collection("lesson").document().id
            val createdBy = firebaseAuth.currentUser?.uid ?: ""
            val createdAt = System.currentTimeMillis()
            val grade = binding.etLessonGrade.text.toString()


            val lesson = Lesson(
                lessonId,
                title,
                grade,
                description,
                videoId,
                Uri.EMPTY.toString(),
                credits,
                createdAt,
                createdBy
            )


            firebaseDatabase.collection("lesson")
                .document(lessonId)
                .set(lesson)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        startActivity(Intent(this, TeacherActivities::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Error: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun extractVideoIdFromSharingLink(shareLink: String) : String?{

        val stringBuilder = StringBuilder()

        if (shareLink.contains("?")){
            stringBuilder.append(shareLink.substring(shareLink.lastIndexOf("/")+1 , shareLink.indexOf("?")))
        }else{
            stringBuilder.append(shareLink.substring(shareLink.lastIndexOf("/")+1 , shareLink.length))
        }

        return stringBuilder.toString()
    }
}