package com.ahmed.ostazMohamed.StudentRecyclerViewsAndRepo

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.ahmed.ostazMohamed.Domain.Lesson
import com.ahmed.ostazMohamed.Utils.FirebaseManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StudentRepository {
    private val fireBaseDataBase = FirebaseManager.db
    private val fireBaseUser = FirebaseManager.auth


    fun onCourseClicked(
        activity: Activity,
        lesson: Lesson,
        onSuccess: (String) -> Unit,
        onError: (Exception, String) -> Unit,
    ) {
        val currentUser = fireBaseUser.currentUser

        val userDocRef =
            fireBaseDataBase.collection("users").document(currentUser?.uid.toString())

        userDocRef.get().addOnSuccessListener { userDocument ->
            val userCredits = userDocument.getLong("credits") ?: 0
            val lessonCreditCost = lesson.creditCost
            val watchedLessons =
                userDocument.get("watchedLessons") as? List<String> ?: emptyList()

            when {
                watchedLessons.contains(lesson.lessonId) -> {
                    // Lesson Already Watched\
                    val bundle = Bundle()
                    bundle.putString("videoId", lesson.videoId)
                    bundle.putString("title", lesson.title)

                    onSuccess("Lesson Already Watched")
                }

                !watchedLessons.contains(lesson.lessonId) && userCredits >= lessonCreditCost -> {
                    val buyConfirmationDialog = AlertDialog.Builder(activity)
                        .setTitle("Do you want to buy this lesson?")
                        .setMessage("Credits : ${lesson.creditCost}")
                        .setPositiveButton("Yes") { _, _ ->
                            fireBaseDataBase.runTransaction { transaction ->
                                val snapshot = transaction.get(userDocRef)
                                val credits = snapshot.getLong("credits") ?: 0
                                val watchedLessons =
                                    snapshot.get("watchedLessons") as? List<String>
                                        ?: emptyList()

                                if (watchedLessons.contains(lesson.lessonId)) {
                                    onError(Exception(), "Lesson already watched")
                                }
                                if (credits < lessonCreditCost) {
                                    onError(Exception(), "Not Enough credits")
                                }
                                transaction.update(
                                    userDocRef,
                                    "credits",
                                    credits - lessonCreditCost
                                )
                                transaction.update(
                                    userDocRef,
                                    "watchedLessons",
                                    FieldValue.arrayUnion(lesson.lessonId)
                                )

                            }.addOnSuccessListener {
                                val bundle = Bundle()
                                bundle.putString("videoId", lesson.videoId)
                                bundle.putString("title", lesson.title)

                                onSuccess("Lesson Bought Successfully")


                            }.addOnFailureListener { e ->
                                onError(e, "Code is used or Wrong Code")
                            }

                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }.create()

                    buyConfirmationDialog.show()

                }

                else -> onError(Exception(), "Not Enough Credits")
            }

        }

    }

    fun onRedeemClicked(
        code: String,
        onSuccess: (String) -> Unit,
        onError: (Exception, String) -> Unit,
    ) {

        val code = code.trim()

        if (code.isEmpty()) {
            onError(Exception(), "Please Enter Code")
            return
        }

        val codeDocRef =
            fireBaseDataBase.collection("codes")
                .document(code)

        val userDocRef = fireBaseDataBase.collection("users")
            .document(fireBaseUser.uid.toString())

        fireBaseDataBase.runTransaction { transaction ->

            val snapshot = transaction.get(codeDocRef)
            val userSnapshot = transaction.get(userDocRef)

            if (!snapshot.exists() || !userSnapshot.exists()) {
                onError(Exception(), "Invalid Code or User")
            }
            val isCodeUsed = snapshot.getBoolean("used") ?: throw FirebaseFirestoreException(
                "Invalid Code", FirebaseFirestoreException.Code.ABORTED
            )

            val credits = snapshot.getLong("credits") ?: throw FirebaseFirestoreException(
                "Invalid Code ",
                FirebaseFirestoreException.Code.ABORTED
            )
            val currentUserCredits =
                userSnapshot.getLong("credits") ?: 0
            if (!isCodeUsed) {
                transaction.update(userDocRef, "credits", credits + currentUserCredits)
                transaction.update(codeDocRef, "used", true)
                transaction.update(codeDocRef, "usedByUid", fireBaseUser.currentUser?.email)
                transaction.update(
                    codeDocRef,
                    "usedAt",
                    date(System.currentTimeMillis().toString())
                )

            } else {
                onError(Exception(), "Code Already Used")
            }

        }.addOnSuccessListener {
            onSuccess("Code Redeemed Successfully")
        }.addOnFailureListener { e ->
            onError(e, "Transaction Failed Please Try Again")
        }
    }

     fun date(time: String): String {
        val currentTime = System.currentTimeMillis()
        val date = Date(currentTime)
        val formatter = SimpleDateFormat("yy/MM/dd", Locale.getDefault())
        val formatterDate = formatter.format(date)
        return formatterDate
    }
}