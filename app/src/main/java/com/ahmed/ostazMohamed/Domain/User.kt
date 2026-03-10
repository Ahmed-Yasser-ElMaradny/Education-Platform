package com.ahmed.ostazMohamed.Domain

import com.ahmed.ostazMohamed.Utils.UserRole

data class User(
    val uid: String?,
    val email: String,
    val role: String = UserRole.Student.name, // "student" / "teacher"
    val displayName: String,
    val credits: Int,
    val photoUrl: String,
    val isBlocked: Boolean,
    val createdAt: Long,
    val watchedLessons: List<String>,
) {
    constructor() : this(
        null,
        "",
        "",
        "",
        0,
        "",
        false,
        0,
        emptyList()
    )
}

