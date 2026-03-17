package com.ahmed.ostazahmed.TeacherFragments.generator

import com.ahmed.ostazahmed.Domain.AccessCode
import com.ahmed.ostazahmed.Utils.FirebaseManager
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GeneratorRepository {
    val fireBaseDatabase = FirebaseManager.db

    fun observeGeneratedCodes(
        onSuccess: (List<AccessCode>) -> Unit,
        onError: (Exception) -> Unit,
    ): ListenerRegistration {

        // التعديل الأول: خلينا الفايربيز يرتب الداتا عشان نوفر مجهود الموبايل
        return fireBaseDatabase.collection("codes")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val codesList = mutableListOf<AccessCode>()

                    for (document in snapshot.documents) {
                        val code = document.toObject(AccessCode::class.java)
                        if (code != null) {
                            codesList.add(code)
                        }
                        // شيلنا الترتيب من هنا لأنه كان جوه اللوب وبيهنج الدنيا
                    }
                    onSuccess(codesList)
                }
            }
    }

    // التعديل التاني: نقلنا الشغل التقيل بتاع التواريخ للـ IO Thread
    suspend fun codeValidation() = withContext(Dispatchers.IO) {
        try {
            // 1. نجيب تاريخ النهاردة بنفس الصيغة بتاعتك بالظبط
            val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val todayString = formatter.format(Date(System.currentTimeMillis()))

            // 2. التريكة هنا: بنخلي الفايربيز هو اللي يفلتر التواريخ القديمة بس! (بدل ما نحمل الكوليكشن كله)
            val expiredCodesSnapshot = fireBaseDatabase.collection("codes")
                .whereLessThan("expiresAt", todayString)
                .get()
                .await()

            // 3. لو ملقاش أكواد منتهية، هيخرج فوراً ومش هيعمل أي لود
            if (expiredCodesSnapshot.isEmpty) return@withContext

            // 4. لو لقى، هيمسحهم في خبطة واحدة (Batch)
            val batch = fireBaseDatabase.batch()
            expiredCodesSnapshot.documents.forEach { documentSnapshot ->
                batch.delete(documentSnapshot.reference)
            }
            batch.commit().await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteAllCodes() = withContext(Dispatchers.IO) {
        val batch = fireBaseDatabase.batch()
        val snapshot = fireBaseDatabase.collection("codes").get().await()
        for (document in snapshot.documents) {
            val docRef = fireBaseDatabase.collection("codes").document(document.id)
            batch.delete(docRef)
        }
        batch.commit().await()
    }

    suspend fun addCodes(accessCode: AccessCode) = withContext(Dispatchers.IO) {
        val batch = fireBaseDatabase.batch()
        val docRef = fireBaseDatabase.collection("codes").document(accessCode.code)
        batch.set(docRef, accessCode)
        batch.commit().await()
    }
}