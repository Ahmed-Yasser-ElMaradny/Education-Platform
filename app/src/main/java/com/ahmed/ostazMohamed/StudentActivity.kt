package com.ahmed.ostazMohamed

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ahmed.ostazMohamed.StudentRecyclerViewsAndRepo.StudentCoursesRecyclerView
import com.ahmed.ostazMohamed.StudentRecyclerViewsAndRepo.StudentViewModel
import com.ahmed.ostazMohamed.TeacherFragments.TeacherCourses.CoursesViewModel
import com.ahmed.ostazMohamed.Utils.FirebaseManager
import com.ahmed.ostazMohamed.databinding.ActivityStudentBinding
import com.google.firebase.auth.FirebaseUser
import kotlin.collections.emptyList

class StudentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudentBinding
    private val fireBaseDataBase = FirebaseManager.db


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val currentUser = FirebaseManager.auth.currentUser!!


        val coursesViewModel = ViewModelProvider(this)[CoursesViewModel::class.java]
        val studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]
        binding.goToCreditsPage.setOnClickListener {
            startActivity(Intent(this, StudentCodeActivity::class.java))
        }

        setupUserInfo(currentUser)
        fetchStudentData(currentUser.uid)

        val adapter = StudentCoursesRecyclerView(emptyList()) { lesson ->
            studentViewModel.onCourseClicked(
                this, lesson,
                onSuccess = { task ->
                    val intent = Intent(this, WatchingLessonActivity::class.java)
                    intent.putExtra("title", lesson.title)
                    intent.putExtra("videoId", lesson.videoId)
                    startActivity(intent)
                    Toast.makeText(this, task, Toast.LENGTH_SHORT).show()
                },
                onError = { exception, task ->
                    Toast.makeText(this, task, Toast.LENGTH_SHORT).show()
                })

        }
        binding.studentCoursesRecyclerView.adapter = adapter
        binding.studentCoursesRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        coursesViewModel.courses.observe(this) { courses ->
            adapter.updateCourse(courses)
        }
        coursesViewModel.observeCourses()

    }


    private fun setupUserInfo(user : FirebaseUser) {

        binding.userEmail.text = user.email
        val userPhotoUrl = user.photoUrl.toString()
        if (userPhotoUrl.isNotEmpty()) {
            Glide.with(this)
                .load(userPhotoUrl)
                .placeholder(R.drawable.user)
                .into(binding.profileImage)
             }else binding.profileImage.setImageResource(R.drawable.user)
        }


    private fun fetchStudentData(uid: String) {
        fireBaseDataBase.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // ✅ الداتا جات سليمة، اعرضها واشتغل عادي جداً
                    binding.userCredits.text = "Credits : ${document.getLong("credits") ?: 0}"
                    binding.userEmail.text = document.getString("email") ?: "No Email"
                }
            }
            .addOnFailureListener {
                // 🔄 لو حصل خطأ (نت فصل أو تهنيجة)، الكود هيحاول يجيب الداتا تاني بعد ثانية من غير ما اليوزر يحس
                binding.userCredits.postDelayed({
                    fetchStudentData(uid)
                }, 1000)
            }
    }
}