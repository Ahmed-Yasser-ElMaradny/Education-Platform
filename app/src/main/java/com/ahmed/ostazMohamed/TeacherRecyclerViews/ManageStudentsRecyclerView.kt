package com.ahmed.ostazMohamed.TeacherRecyclerViews

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmed.ostazMohamed.Domain.User
import com.ahmed.ostazMohamed.R
import com.ahmed.ostazMohamed.databinding.ItemStudentBinding

class ManageStudentsRecyclerView(
    var users: List<User>,
    val onBlock: (User) -> Unit,
    val onRecover: (User) -> Unit,
) : RecyclerView.Adapter<ManageStudentsRecyclerView.ManageStudentsViewHolder>() {

    inner class ManageStudentsViewHolder(val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ManageStudentsViewHolder {
        val binding =
            ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ManageStudentsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ManageStudentsViewHolder, position: Int) {
        val  user = users[position]
        holder.binding.studentNameText.text = user.displayName
        holder.binding.studentAccountText.text = user.email
        holder.binding.creditsText.text = "User Credits : " + user.credits.toString()

        holder.binding.buttonBlockStudent.setOnClickListener {
            onBlock(user)
            holder.binding.chip4.text = "Blocked"
            holder.binding.chip4.setChipBackgroundColorResource(R.color.teal_200)
        }
        holder.binding.buttonRecoverStudent.setOnClickListener {
            onRecover(user)
            holder.binding.chip4.text = "Not Blocked"
            holder.binding.chip4.setChipBackgroundColorResource(R.color.light_purple)
        }
    }

    override fun getItemCount(): Int = users.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateUsersList(students: List<User>){
        users = students
        notifyDataSetChanged()
    }
}