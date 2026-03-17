package com.ahmed.ostazahmed.TeacherRecyclerViews

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ahmed.ostazahmed.Domain.AccessCode
import com.ahmed.ostazahmed.databinding.ItemCodeBinding
import com.google.firebase.auth.FirebaseAuth
// ضيف مسار الـ R والـ ItemCodeBinding بتاعك هنا

class TeacherGeneratedCodesAdapter(
    val onCodeCopied: (AccessCode) -> Unit,
    val onDelete: (AccessCode) -> Unit
) : ListAdapter<AccessCode, TeacherGeneratedCodesAdapter.GeneratedCodesViewHolder>(CodesDiffCallback()) {

    private val currentUser = FirebaseAuth.getInstance().currentUser

    inner class GeneratedCodesViewHolder(val itemCodeBinding: ItemCodeBinding) :
        RecyclerView.ViewHolder(itemCodeBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneratedCodesViewHolder {
        val itemCodeBinding = ItemCodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GeneratedCodesViewHolder(itemCodeBinding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GeneratedCodesViewHolder, position: Int) {
        // في الـ ListAdapter بنستخدم getItem(position) بدل اللستة اليدوية
        val codePosition = getItem(position)

        holder.itemCodeBinding.tvCode.text = codePosition.code
        holder.itemCodeBinding.tvCredits.text = "Credits : ${codePosition.credits}"
        holder.itemCodeBinding.tvExpiry.text = "Expiry Date : ${codePosition.expiresAt}"
        holder.itemCodeBinding.usedByText.text = "Used By : ${codePosition.usedByUid}"
        holder.itemCodeBinding.createdByText.text = "Created By : ${currentUser?.email}"
        holder.itemCodeBinding.usedAtText.text = "Used At : ${codePosition.usedAt}"

        holder.itemCodeBinding.chipStatus.text = if (codePosition.usedByUid.isNotEmpty()) "Used" else "Active"

        // ظبط مسار الألوان بتاعك هنا
        holder.itemCodeBinding.chipStatus.setChipBackgroundColorResource(
            if (codePosition.usedByUid.isNotEmpty()) android.R.color.holo_red_light else android.R.color.holo_purple
        )

        holder.itemCodeBinding.btnCopy.setOnClickListener {
            onCodeCopied(codePosition)
        }
        holder.itemCodeBinding.btnDelete.setOnClickListener {
            onDelete(codePosition)
        }
    }
}

// الكلاس ده هو السحر اللي بيقارن اللستة القديمة بالجديدة عشان ميهنجش
class CodesDiffCallback : DiffUtil.ItemCallback<AccessCode>() {
    override fun areItemsTheSame(oldItem: AccessCode, newItem: AccessCode): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: AccessCode, newItem: AccessCode): Boolean {
        return oldItem == newItem
    }
}