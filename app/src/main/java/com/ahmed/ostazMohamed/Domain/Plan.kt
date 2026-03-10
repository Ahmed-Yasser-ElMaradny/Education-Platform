package com.ahmed.ostazMohamed.Domain

import com.ahmed.ostazMohamed.Utils.PlanType

data class Plan(
    val planId: String ,
    val gradeId: String ,
    val name: String ,                   // "باقة 4 حصص" / "سنوية 48"
    val type: String = PlanType.Credits.name, // "credits" / "unlimited"
    val totalCredits: Int ,               // مهم لو credits
    val durationDays: Int ,               // 30/365... أو 0
    val isActive: Boolean ,
    val createdAt: Long ,
    val description: String
)
