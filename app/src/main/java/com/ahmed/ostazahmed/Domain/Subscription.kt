package com.ahmed.ostazahmed.Domain

import com.ahmed.ostazahmed.Utils.SubscriptionStatus

data class Subscription(
    val userUid: String ,
    val planId: String ,
    val gradeId: String ,
    val status: String = SubscriptionStatus.Active.name, // active/expired/canceled
    val startsAt: Long ,
    val endsAt: Long , // لو durationDays=0 ممكن تسيبه 0
    val totalCredits: Int ,       // للعرض
    val remainingCredits: Int ,   // اللي هينقص مع فتح الحصص
    val activatedByCode: String ,
    val updatedAt: Long
)