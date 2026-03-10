package com.ahmed.ostazMohamed.TeacherFragments.generator

import com.ahmed.ostazMohamed.Domain.AccessCode
import com.ahmed.ostazMohamed.Utils.FirebaseManager
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date

class GeneratorRepository {
    val fireBaseDatabase = FirebaseManager.db
    private var listenerRegistration: ListenerRegistration? = null

    fun observeGeneratedCodes(
        onSuccess: (List<AccessCode>) -> Unit,
        onError: (Exception) -> Unit,
    ): ListenerRegistration {

        return fireBaseDatabase.collection("codes").addSnapshotListener { snapshot, error ->
            if (error != null) {
                onError(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val codesList = mutableListOf<AccessCode>()

                for (document in snapshot.documents) {
                    val code = document.toObject(AccessCode::class.java)

                    if (code != null)
                        codesList.add(code)

                    codesList.sortByDescending { it.createdAt }
                    onSuccess(codesList)
                }
            }
        }
    }

    suspend fun codeValidation() {
        val currentDate = System.currentTimeMillis()
        val snapshot = fireBaseDatabase.collection("codes").get().await()

        snapshot.documents.forEach { documentSnapshot ->
            val accessCode = documentSnapshot.toObject(AccessCode::class.java)
            if (accessCode != null) {
                val expiryDate = SimpleDateFormat("yyyy/MM/dd").parse(accessCode.expiresAt)

                if (expiryDate != null && expiryDate.before(Date(currentDate))) {
                    fireBaseDatabase.collection("codes").document(accessCode.code).delete().await()
                }
            }
        }
    }

    suspend fun deleteAllCodes(onSuccess: (List<AccessCode>) -> Unit, onError: (Exception) -> Unit) {
        val batch = fireBaseDatabase.batch()

        val snapshot = fireBaseDatabase.collection("codes").get().await()
        for (document in snapshot.documents){
            val docRef = fireBaseDatabase.collection("codes").document(document.id)
            batch.delete(docRef)
        }
        batch.commit().await()
        return onSuccess(emptyList())
    }

    suspend fun addCodes(accessCode: AccessCode){
        val batch = fireBaseDatabase.batch()

        val docRef = fireBaseDatabase.collection("codes").document(accessCode.code)
        batch.set(docRef, accessCode)

        batch.commit().await()
    }
}