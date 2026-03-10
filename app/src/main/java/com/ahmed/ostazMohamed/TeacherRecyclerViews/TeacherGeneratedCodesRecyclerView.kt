package com.ahmed.ostazMohamed.TeacherRecyclerViews

import android.R
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmed.ostazMohamed.Domain.AccessCode
import com.ahmed.ostazMohamed.databinding.ItemCodeBinding
import com.google.firebase.auth.FirebaseAuth

private val currentUser = FirebaseAuth.getInstance().currentUser
class TeacherGeneratedCodesRecyclerView(var codes: List<AccessCode>, val  onCodeCopied : (AccessCode) -> Unit , val onDelete : (AccessCode) -> Unit) :
    RecyclerView.Adapter<TeacherGeneratedCodesRecyclerView.GeneratedCodesViewHolder>() {

    inner class GeneratedCodesViewHolder(val itemCodeBinding: ItemCodeBinding) :
        RecyclerView.ViewHolder(itemCodeBinding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GeneratedCodesViewHolder {
        val itemCodeBinding =
            ItemCodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GeneratedCodesViewHolder(itemCodeBinding)

    }

    override fun onBindViewHolder(
        holder: GeneratedCodesViewHolder,
        position: Int
    ) {
        val codePosition = codes[position]
        holder.itemCodeBinding.tvCode.text = codePosition.code
        holder.itemCodeBinding.tvCredits.text ="Credits : ${codePosition.credits}"
        holder.itemCodeBinding.tvExpiry.text = "Expiry Date : ${codePosition.expiresAt}"
        holder.itemCodeBinding.usedByText.text = "Used By : ${codePosition.usedByUid}"
        holder.itemCodeBinding.createdByText.text = "Created By : ${currentUser?.email}"
        holder.itemCodeBinding.usedAtText.text = "Used At : ${codePosition.usedAt}"
        holder.itemCodeBinding.chipStatus.text = if (codePosition.usedByUid.isNotEmpty()) "Used" else "Active"
        holder.itemCodeBinding.chipStatus.setChipBackgroundColorResource(if (codePosition.usedByUid.isNotEmpty()) R.color.holo_red_light else com.ahmed.ostazMohamed.R.color.light_purple)
        holder.itemCodeBinding.btnCopy.setOnClickListener {
            onCodeCopied(codePosition)
        }
        holder.itemCodeBinding.btnDelete.setOnClickListener {
            onDelete(codePosition)
        }


    }

    override fun getItemCount(): Int = codes.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newCodes: List<AccessCode>) {
        codes = newCodes
        notifyDataSetChanged()
    }

}