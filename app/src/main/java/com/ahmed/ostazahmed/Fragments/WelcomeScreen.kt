package com.ahmed.ostazahmed.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ahmed.ostazahmed.AuthenticationHelper.AppNavigator
import com.ahmed.ostazahmed.AuthenticationHelper.AuthViewModel
import com.ahmed.ostazahmed.R
import com.ahmed.ostazahmed.Utils.FirebaseManager
import com.ahmed.ostazahmed.databinding.WelcomeFragmentBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException


class WelcomeScreen : Fragment() {

    private var _binding: WelcomeFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var authViewModel: AuthViewModel

    // المشغل بتاع جوجل
    private val googleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val token = account.getResult(ApiException::class.java).idToken
                if (token != null) {
                    authViewModel.signInWithGoogle(token) { success, error ->
                        if (success) {
                            val userId = FirebaseManager.auth.currentUser?.uid
                            if (userId != null) {
                                authViewModel.getUserRole(userId) { role, isBlocked ->
                                    if (role != null) {
                                        AppNavigator.navigateToRoleDestination(requireActivity(), role, isBlocked)
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(requireContext(), "Sign in failed: $error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: ApiException) {
                Toast.makeText(requireContext(), "Google sign in cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = WelcomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        initGoogleSignIn()

        binding.btnSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeScreen_to_signInFragment)
        }
        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeScreen_to_signUpFragment)
        }

        binding.googleButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleLauncher.launch(signInIntent)
        }
        binding.infoButton.setOnClickListener {
            infoButton()
        }
    }

    // صلحنا استدعاءات جوجل اللي كانت بتعمل Error
    private fun initGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun infoButton(){
        val infoDialog = AlertDialog.Builder(requireContext())
            .setTitle("معلومات التواصل :")
            .setMessage("Phone : 01001134349 \ninstagram : instagram.com/mr.hanyelhussiny")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        infoDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}