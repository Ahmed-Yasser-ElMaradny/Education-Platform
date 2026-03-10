package com.ahmed.ostazMohamed.TeacherRecyclerViews

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmed.ostazMohamed.Domain.Lesson
import com.ahmed.ostazMohamed.R
import com.ahmed.ostazMohamed.databinding.ItemLessonTeacherBinding

class TeacherCoursesRecyclerView(
    var coursesList: List<Lesson>,
    private val onDelete: (Lesson) -> Unit,
    private val onUpdate: (Lesson) -> Unit
) : RecyclerView.Adapter<TeacherCoursesRecyclerView.ViewHolder>() {

    inner class ViewHolder(val binding: ItemLessonTeacherBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemLessonTeacherBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val course = coursesList[position]
        holder.binding.tvLessonTitle.text = course.title
        holder.binding.tvLessonGrade.text = course.grade
        holder.binding.tvLessonInfo.text = buildString {
            append("Credits : ")
            append(course.creditCost.toString())
        }
        holder.binding.tvLessonDescription.text = course.description
        holder.binding.imgLesson.setImageResource(R.drawable.course_image)

        holder.binding.btnDeleteLesson.setOnClickListener {
            onDelete(course)
        }
        holder.binding.btnModifyLesson.setOnClickListener {
            onUpdate(course)
        }
    }

    override fun getItemCount(): Int = coursesList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newCourses: List<Lesson>) {
        coursesList = newCourses
        notifyDataSetChanged()

    }

}