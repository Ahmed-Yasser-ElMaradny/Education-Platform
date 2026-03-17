package com.ahmed.ostazahmed.AuthenticationHelper

object InputValidator {
    val allowedDomains = listOf("@gmail.com", "@icloud.com", "@yahoo.com")

    fun isValidEmail(email: String): Boolean {
        return allowedDomains.any { email.endsWith(it) }
    }

    fun isStrongPassword(password: String): Boolean {
        val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
        return regex.matches(password)
    }
}