package com.ahmed.ostazahmed

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ahmed.ostazahmed.StudentRecyclerViewsAndRepo.StudentCoursesRecyclerView
import com.ahmed.ostazahmed.StudentRecyclerViewsAndRepo.StudentViewModel
import com.ahmed.ostazahmed.TeacherFragments.TeacherCourses.CoursesViewModel
import com.ahmed.ostazahmed.Utils.FirebaseManager
import com.ahmed.ostazahmed.databinding.ActivityStudentBinding
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

        // 1. جهزنا اللستة (يفضل تحط "الكل" عشان الطالب يقدر يشوف كل الكورسات لو حابب)
        val filterGrades = arrayOf("الكل", "الصف الأول الثانوي", "الصف الثاني الثانوي", "الصف الثالث الثانوي")

        // 2. عملنا الـ Adapter
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, filterGrades)

        // 3. ربطناه بالـ Spinner
        binding.spinnerFilterGrade.adapter = spinnerAdapter

        // ==========================================
        // 🚨 التعديل السحري هنا (المراقب اللي بيحس بتغيير السنة) 🚨
        // ==========================================
        binding.spinnerFilterGrade.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                // بننادي الـ ViewModel ونديله رقم السنة اللي الطالب اختارها عشان يفلتر
                coursesViewModel.observeCourses(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // لو محصلش أي اختيار، نعرض "الكل" افتراضياً
                coursesViewModel.observeCourses(1)
            }
        }

        // ==========================================
        // باقي كود الـ Adapter بتاعك زي ما هو بالظبط ✔️
        // ==========================================
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
        binding.studentCoursesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        coursesViewModel.courses.observe(this) { courses ->
            adapter.updateCourse(courses)
        }

        // 🚨 ملحوظة: أنا مسحت coursesViewModel.observeCourses() اللي كانت مكتوبة هنا لوحدها
        // لأن الـ Spinner أول ما بيشتغل بيناديها لوحده (أول اختيار بيكون "الكل" position 0)

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