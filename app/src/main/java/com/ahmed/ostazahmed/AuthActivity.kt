package com.ahmed.ostazahmed


import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.facebook.appevents.AppEventsLogger
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.ahmed.ostazahmed.AuthenticationHelper.AppNavigator
import com.facebook.FacebookSdk
import com.ahmed.ostazahmed.AuthenticationHelper.AuthViewModel
import com.ahmed.ostazahmed.Utils.FirebaseManager
import com.ahmed.ostazahmed.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val firebaseDataBase = Firebase.firestore


    override fun onStart() {
        super.onStart()
        updateUi()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FacebookSdk.setClientToken(getString(R.string.facebook_client_token))
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(application)
        enableEdgeToEdge()
        setupUi()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    fun updateUi() {
        val authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        if (FirebaseManager.currentUserUid != null) {
            val userId = FirebaseManager.currentUserUid.toString()
            authViewModel.getUserRole(userId) { role, isBlocked ->
                if (role!=null){
                    AppNavigator.navigateToRoleDestination(this , role , isBlocked)
                }
            }
        }
    }

    fun setupUi() {
        supportActionBar?.hide()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())

    }
}
