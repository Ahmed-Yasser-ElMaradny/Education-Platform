package com.ahmed.ostazahmed.TeacherFragments.TeacherCourses


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmed.ostazahmed.AddCourseActivity
import com.ahmed.ostazahmed.Domain.Lesson
import com.ahmed.ostazahmed.TeacherRecyclerViews.TeacherCoursesRecyclerView
import com.ahmed.ostazahmed.Utils.FirebaseManager
import com.ahmed.ostazahmed.databinding.FragmentTeacherCoursesBinding
import com.ahmed.ostazahmed.databinding.ModifyLessonLayoutBinding

class CoursesFragment : Fragment() {

    private var _binding: FragmentTeacherCoursesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val fireBaseDatabase = FirebaseManager.db

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentTeacherCoursesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val coursesViewModel =
            ViewModelProvider(this)[CoursesViewModel::class.java]

        val adapter = TeacherCoursesRecyclerView(
            emptyList(),
            onDelete = { lesson ->
                onDeleteItemClicked(lesson)
            },
            onUpdate = { lesson ->
                onUpdateItemClicked(lesson)
            })


        binding.rvCourses.adapter = adapter
        binding.rvCourses.layoutManager = LinearLayoutManager(requireContext())
        coursesViewModel.courses.observe(viewLifecycleOwner) { course ->
            adapter.updateData(course)
        }
        coursesViewModel.observeCourses()

        binding.addCourse.setOnClickListener {
            val intent = Intent(context, AddCourseActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onDeleteItemClicked(lesson: Lesson) {

        fireBaseDatabase.collection("lesson").document(lesson.lessonId).delete()
    }


    @SuppressLint("SuspiciousIndentation")
    private fun onUpdateItemClicked(lesson: Lesson) {
        // 1. نفخ شاشة التعديل (Inflate)
        val layoutInflater = ModifyLessonLayoutBinding.inflate(LayoutInflater.from(requireContext()), null, false)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(layoutInflater.root).create()

        // 2. وضع الداتا القديمة في الحقول عشان المدرس يشوفها ويعدل عليها
        layoutInflater.etLessonNameModify.setText(lesson.title)
        layoutInflater.etDescriptionModify.setText(lesson.description)
        layoutInflater.etCreditsModify.setText(lesson.creditCost.toString())
        layoutInflater.etVideoIdModify.setText(lesson.videoId)

        // 3. تجهيز القائمة المنسدلة (Spinner) للصفوف الدراسية
        val gradesList = arrayOf("الصف الأول الثانوي", "الصف الثاني الثانوي", "الصف الثالث الثانوي")
        // استخدمنا requireContext() بدل this
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, gradesList)
        layoutInflater.spinnerLessonGradeModify.setAdapter(spinnerAdapter)

        // تريكة السينيور: وضع القيمة القديمة بتاعة الكورس في القايمة من غير ما تفتح أوتوماتيك (false)
        layoutInflater.spinnerLessonGradeModify.setText(lesson.grade, false)

        // إظهار الشاشة المنبثقة
        dialog.show()

        // 4. برمجة زرار الحفظ بعد التعديل
        layoutInflater.btnModifyLesson.setOnClickListener {

            // سحب الداتا من الحقول
            val title = layoutInflater.etLessonNameModify.text.toString().trim()
            val description = layoutInflater.etDescriptionModify.text.toString().trim()
            val creditsText = layoutInflater.etCreditsModify.text.toString().trim()
            val rawVideoId = layoutInflater.etVideoIdModify.text.toString().trim()
            val grade = layoutInflater.spinnerLessonGradeModify.text.toString().trim()

            // 5. الفحص والـ Validation
            val errorMessage = when {
                title.isEmpty() -> "من فضلك اكتب اسم الدرس"
                description.isEmpty() -> "من فضلك اكتب الوصف"
                creditsText.isEmpty() -> "من فضلك اكتب عدد الكريدتس"
                creditsText.toIntOrNull() == null -> "الكريدتس لازم تكون أرقام فقط"
                creditsText.toInt() < 0 -> "أقل عدد كريدتس مسموح هو 1"
                rawVideoId.isEmpty() -> "من فضلك اكتب Video ID"
                grade.isEmpty() -> "من فضلك اختار الصف الدراسي"
                else -> null
            }

            // لو في Error وقف التنفيذ وطلع رسالة
            if (errorMessage != null) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // =========================
            // DATA VALID ✔
            // =========================

            val credits = creditsText.toInt()

            // استخراج الـ Video ID (لو المستخدم حط لينك كامل بدل الـ ID)
            val extractedVideoId = extractVideoIdFromSharingLink(rawVideoId) ?: rawVideoId

            // تحويل الصف الدراسي لرقم (عشان الداتابيز تبقى خفيفة وسريعة في الفلتر)
            val academicYear = when(grade){
                "الصف الأول الثانوي" -> 1
                "الصف الثاني الثانوي" -> 2
                "الصف الثالث الثانوي" -> 3
                else -> 1 // قيمة افتراضية
            }

            // بناء كائن الحصة (Lesson) الجديد بعد التعديل
            val modifiedLesson = Lesson(
                lessonId = lesson.lessonId, // بنحتفظ بنفس الـ ID عشان نعدل عليه ميعملش واحد جديد
                title = title,
                grade = grade, // بنحفظ الاسم العربي
                description = description,
                videoId = extractedVideoId,
                thumbnailUrl = lesson.thumbnailUrl, // بنحتفظ بالصورة القديمة
                creditCost = credits,
                createdAt = lesson.createdAt, // بنحتفظ بوقت الإنشاء الأصلي
                createdBy = lesson.createdBy, // بنحتفظ باللي أنشأها
                academicYear = academicYear // الفيلد الجديد بتاع الرقم (مهم جداً للفلتر)
            )

            // 6. الرفع للـ Firebase
            fireBaseDatabase.collection("lesson").document(lesson.lessonId)
                .set(modifiedLesson)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "تم تعديل الحصة بنجاح", Toast.LENGTH_SHORT).show()
                        dialog.dismiss() // اقفل الشاشة بعد النجاح
                    } else {
                        Toast.makeText(requireContext(), "حدث خطأ: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
     private fun extractVideoIdFromSharingLink(shareLink: String) : String? {
        val stringBuilder = StringBuilder()

         if (shareLink.contains("?")){
             stringBuilder.append(shareLink.substring(shareLink.lastIndexOf("/")+1 , shareLink.lastIndexOf("?")))

         }else {
             stringBuilder.append(shareLink.substring(shareLink.lastIndexOf("/")+1 , shareLink.length))
         }

         return stringBuilder.toString()

     }
}


