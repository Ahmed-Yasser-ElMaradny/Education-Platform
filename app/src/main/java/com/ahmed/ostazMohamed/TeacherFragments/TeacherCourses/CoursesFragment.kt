package com.ahmed.ostazMohamed.TeacherFragments.TeacherCourses


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmed.ostazMohamed.AddCourseActivity
import com.ahmed.ostazMohamed.Domain.Lesson
import com.ahmed.ostazMohamed.TeacherRecyclerViews.TeacherCoursesRecyclerView
import com.ahmed.ostazMohamed.Utils.FirebaseManager
import com.ahmed.ostazMohamed.databinding.FragmentTeacherCoursesBinding
import com.ahmed.ostazMohamed.databinding.ModifyLessonLayoutBinding

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
        val layoutInflater =
            ModifyLessonLayoutBinding.inflate(LayoutInflater.from(requireContext()), null, false)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(layoutInflater.root).create()

        layoutInflater.etLessonNameModify.setText(lesson.title)
        layoutInflater.etDescriptionModify.setText(lesson.description)
        layoutInflater.etCreditsModify.setText(lesson.creditCost.toString())
        layoutInflater.etVideoIdModify.setText(lesson.videoId)
        layoutInflater.etLessonGrade.setText(lesson.grade)
        dialog.show()

        layoutInflater.btnModifyLesson.setOnClickListener {
            val modifiedLesson = Lesson(
                lessonId = lesson.lessonId,
                title = layoutInflater.etLessonNameModify.text.toString(),
                grade = layoutInflater.etLessonGrade.text.toString(),
                description = layoutInflater.etDescriptionModify.text.toString(),
                videoId = extractVideoIdFromSharingLink(layoutInflater.etVideoIdModify.text.toString())!!,
                thumbnailUrl = lesson.thumbnailUrl,
                creditCost = layoutInflater.etCreditsModify.text.toString().toInt(),
                createdAt = lesson.createdAt,
                createdBy = lesson.createdBy
            )
            fireBaseDatabase.collection("lesson").document(lesson.lessonId)
                .set(modifiedLesson).addOnCompleteListener { task ->
                    dialog.dismiss()
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


