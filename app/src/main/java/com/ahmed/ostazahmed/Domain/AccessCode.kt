package com.ahmed.ostazahmed.Domain

data class AccessCode(
    val code: String,
    val credits: Int,
    val isUsed: Boolean,
    val usedByUid: String,
    val usedAt: String,
    val expiresAt: String, // 0 يعني بدون انتهاء
    val createdAt: String,
    val createdBy: String?  // uid المدرس
){
    constructor(): this("" , 0 , false , "" , "" , "" , "" , "")
}
