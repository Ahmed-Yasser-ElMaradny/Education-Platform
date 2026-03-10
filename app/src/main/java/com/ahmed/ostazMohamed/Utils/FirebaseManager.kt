package com.ahmed.ostazMohamed.Utils

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

object FirebaseManager {
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val db: FirebaseFirestore by lazy { Firebase.firestore }

    val currentUserUid: String?
        get() = auth.currentUser?.uid
}