package com.ahmed.ostazMohamed.AuthenticationHelper

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.ahmed.ostazMohamed.StudentActivity
import com.ahmed.ostazMohamed.TeacherActivities
import com.ahmed.ostazMohamed.Utils.FirebaseManager
import com.ahmed.ostazMohamed.Utils.UserRole

object AppNavigator {
    private val roleDestination = mapOf(
        UserRole.Teacher.name to TeacherActivities::class.java,
        UserRole.Student.name to StudentActivity::class.java
    )

    fun navigateToRoleDestination(activity : Activity , role : String , isBlocked : Boolean){
        if (isBlocked){
            Toast.makeText(activity , "Your account is blocked" , Toast.LENGTH_SHORT).show()
            FirebaseManager.auth.signOut()
            return
        }else{
            val destination = roleDestination[role]
            if (destination != null) {
               val intent = Intent(activity , destination)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                activity.startActivity(intent)
            }else Toast.makeText(activity , "Error : Role not found" , Toast.LENGTH_SHORT).show()
        }

    }
}