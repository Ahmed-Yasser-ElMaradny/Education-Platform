package com.ahmed.ostazahmed.StudentRecyclerViewsAndRepo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmed.ostazahmed.Domain.Lesson
import com.ahmed.ostazahmed.R
import com.ahmed.ostazahmed.databinding.ItemLessonTeacherBinding
import com.bumptech.glide.Glide

class StudentCoursesRecyclerView(var courses: List<Lesson>, val onClick: (Lesson) -> Unit) :
    RecyclerView.Adapter<StudentCoursesRecyclerView.CoursesViewHolder>() {

    inner class CoursesViewHolder(val binding : ItemLessonTeacherBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CoursesViewHolder {
        val binding = ItemLessonTeacherBinding.inflate(LayoutInflater.from(parent.context ) , parent , false)
        return CoursesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoursesViewHolder, position: Int) {
        val course = courses[position]
        val thumbnailUrl = "https://img.youtube.com/vi/${course.videoId}/hqdefault.jpg"
        holder.binding.tvLessonTitle.text = course.title
        holder.binding.tvLessonGrade.text = " Grade : " + course.grade
        holder.binding.tvLessonDescription.text = course.description
        holder.binding.tvLessonInfo.text =  " Cost : " + course.creditCost.toString()
        Glide.with(holder.itemView.context)
            .load(thumbnailUrl)
            .placeholder(R.drawable.elhusseiny)
            .into(holder.binding.imgLesson)
        holder.binding.btnDeleteLesson.visibility = View.GONE
        holder.binding.btnModifyLesson.visibility = View.GONE
        holder.binding.root.setOnClickListener {
            onClick(course)
        }
    }

    override fun getItemCount(): Int = courses.size

    fun updateCourse(newCourses : List<Lesson>){
        courses = newCourses
        notifyDataSetChanged()
    }
}
