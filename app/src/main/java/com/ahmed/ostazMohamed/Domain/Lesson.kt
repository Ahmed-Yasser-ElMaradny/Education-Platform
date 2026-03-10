package com.ahmed.ostazMohamed.Domain

import android.net.Uri

data class Lesson(
    val lessonId: String,
    val title: String,
    val grade: String,
    val description: String,
    val videoId: String,       // YouTube video ID فقط
    val thumbnailUrl: String,
    val creditCost: Int,
    val createdAt: Long,
    val createdBy: String      // uid المدرس
) {
    constructor() : this("", "","", "", "", Uri.EMPTY.toString(), 0, 0, "")
}

