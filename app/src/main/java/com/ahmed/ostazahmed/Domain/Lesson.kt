package com.ahmed.ostazahmed.Domain



data class Lesson(
    val lessonId: String,
    val title: String,
    val grade: String,
    val description: String,
    val videoId: String,       // YouTube video ID فقط
    val thumbnailUrl: String,
    val creditCost: Int,
    val createdAt: Long,
    val createdBy: String,
    val  academicYear : Int, // uid المدرس
) {
    constructor() : this("", "","", "", "", "", 0, 0, "" , 1)
}

