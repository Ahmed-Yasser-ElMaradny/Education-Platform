package com.ahmed.ostazMohamed

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.ahmed.ostazMohamed.databinding.ActivityTeacherActivitiesBinding
import androidx.fragment.app.Fragment
import com.ahmed.ostazMohamed.TeacherFragments.TeacherCourses.CoursesFragment
import com.ahmed.ostazMohamed.TeacherFragments.generator.GenerateCodesFragment
import com.ahmed.ostazMohamed.TeacherFragments.studentsManagement.StudentsManagementFragment

class TeacherActivities : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherActivitiesBinding

    // 1. التعديل الأول والأهم:
    // بنعرف الفراجمنتس هنا مرة واحدة بس عشان نستخدم نفس النسخة طول الوقت
    // مش كل مرة نعمل نسخة جديدة
    private val coursesFragment = CoursesFragment()
    private val generateCodesFragment = GenerateCodesFragment()
    private val studentsFragment = StudentsManagementFragment()

    // بنخلي الافتراضي هو الكورسات
    private var activeFragment: Fragment = coursesFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTeacherActivitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // لو السطر ده بيعمل مشاكل مع الليي اوت شيله مؤقتا
        WindowCompat.setDecorFitsSystemWindows(window, false)

        supportActionBar?.hide()

        // 2. التعديل الثاني: إضافة النسخ اللي عرفناها فوق
        supportFragmentManager.beginTransaction().apply {
            // بنضيفهم كلهم مرة واحدة
            add(R.id.details_container, studentsFragment, "3").hide(studentsFragment)
            add(R.id.details_container, generateCodesFragment, "2").hide(generateCodesFragment)
            add(R.id.details_container, coursesFragment, "1") // ده الوحيد اللي مش بنخفيه عشان يظهر في الأول
        }.commit()

        // 3. التعديل الثالث: لما نضغط، بننادي على المتغير اللي فوق (مش بنعمل ()New Fragment)
        binding.navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_courses -> {
                    changeFragment(coursesFragment) // استخدمنا المتغير coursesFragment
                    true
                }
                R.id.navigation_generate_codes -> {
                    changeFragment(generateCodesFragment) // استخدمنا المتغير generateCodesFragment
                    true
                }
                R.id.navigation_students_management -> {
                    changeFragment(studentsFragment) // استخدمنا المتغير studentsFragment
                    true
                }
                else -> false
            }
        }
    }

    private fun changeFragment(targetFragment: Fragment) {
        // بنشيك عشان لو ضغط على نفس التاب مرتين ميعملش حاجة
        if (targetFragment == activeFragment) return

        supportFragmentManager.beginTransaction()
            .hide(activeFragment) // اخفي القديم
            .show(targetFragment) // اظهر الجديد (اللي هو مضاف أصلاً من الأول)
            .commit()

        activeFragment = targetFragment
    }
}
