import com.ahmed.ostazahmed.Domain.User
import com.ahmed.ostazahmed.Utils.FirebaseManager
import com.ahmed.ostazahmed.Utils.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class AuthRepository {

    // 1. استخدمنا הـ Singleton اللي عملناه قبل كده
    private val fireBaseDataBase = FirebaseManager.db
    private val auth = FirebaseManager.auth

    // دالة جوجل (زي ما صلحناها قبل كده)
    private fun checkAndSaveGoogleUser(user: FirebaseUser?, onResult: (Boolean, String?) -> Unit) {
        if (user == null) {
            onResult(false, "User is null")
            return
        }
        val userRef = fireBaseDataBase.collection("users").document(user.uid)
        userRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                val newUser = User(
                    uid = user.uid,
                    email = user.email.toString(),
                    role = UserRole.Student.name,
                    displayName = user.displayName ?: "Student",
                    credits = 0,
                    photoUrl = user.photoUrl.toString(),
                    isBlocked = false,
                    createdAt = System.currentTimeMillis(),
                    watchedLessons = emptyList()
                )
                userRef.set(newUser).addOnCompleteListener { task ->
                    onResult(task.isSuccessful, task.exception?.message)
                }
            } else onResult(true, null)
        }.addOnFailureListener { exception ->
            onResult(false, exception.message)
        }
    }

    fun signInWithGoogle(idToken: String, onResult: (Boolean, String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                checkAndSaveGoogleUser(auth.currentUser, onResult)
            } else {
                onResult(false, task.exception?.message)
            }
        }
    }

    fun signIn(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            onResult(task.isSuccessful, task.exception?.message)
        }
    }

    // 2. صلحنا كارثة إنشاء الحساب (مبنسجلش في الداتا بيز إلا لو الحساب اتعمل بنجاح)
    fun signUp(displayName: String, email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { authResult ->
            if (authResult.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    val newUser = User(
                        uid = user.uid,
                        email = user.email.toString(),
                        role = UserRole.Student.name,
                        displayName = displayName, // خدنا الاسم اللي الطالب كتبه
                        credits = 0,
                        photoUrl = user.photoUrl?.toString() ?: "",
                        isBlocked = false,
                        createdAt = System.currentTimeMillis(),
                        watchedLessons = emptyList()
                    )
                    fireBaseDataBase.collection("users").document(user.uid)
                        .set(newUser).addOnCompleteListener { task ->
                            onResult(task.isSuccessful, task.exception?.message)
                        }
                }
            } else {
                onResult(false, authResult.exception?.message)
            }
        }
    }

    fun getUserRole(userId: String, onResult: (String?, Boolean) -> Unit) {
        fireBaseDataBase.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role")
                    val isBlocked = document.getBoolean("blocked") ?: false
                    onResult(role, isBlocked)
                } else {
                    onResult(null, false)
                }
            }.addOnFailureListener {
                onResult(null, false)
            }
    }

    fun resetPassword(email: String, onResult: (Boolean, String) -> Unit) {
        val auth = FirebaseAuth.getInstance()

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // الإيميل اتبعت بنجاح
                    onResult(true, "تم إرسال رابط تعديل كلمة المرور إلى بريدك الإلكتروني")
                } else {
                    // حصل خطأ (مثلاً الإيميل مش متسجل أصلاً)
                    onResult(false, task.exception?.message ?: "حدث خطأ ما")
                }
            }
    }
}