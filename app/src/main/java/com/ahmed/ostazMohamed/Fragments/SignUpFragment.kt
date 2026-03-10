package com.ahmed.ostazMohamed.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ahmed.ostazMohamed.AuthenticationHelper.AppNavigator
import com.ahmed.ostazMohamed.AuthenticationHelper.AuthViewModel
import com.ahmed.ostazMohamed.AuthenticationHelper.InputValidator
import com.ahmed.ostazMohamed.R
import com.ahmed.ostazMohamed.Utils.FirebaseManager
import com.ahmed.ostazMohamed.databinding.SignUpFragmentBinding

class SignUpFragment : Fragment(R.layout.sign_up_fragment) {
    private var _binding: SignUpFragmentBinding? = null
    private val binding get() = _binding!!

    private val firebaseDataBase = FirebaseManager.db



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = SignUpFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.SignInText.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
        }
        val authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.signUpBtn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val displayName = binding.etName.text.toString()

            if (!InputValidator.isValidEmail(email)) {
                Toast.makeText(requireContext(), "Invalid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!InputValidator.isStrongPassword(password)) {
                Toast.makeText(
                    requireContext(),
                    "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one digit",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            authViewModel.signUp(displayName, email, password) { success, error ->
                if (success) {
                    val userId = FirebaseManager.currentUserUid
                    if (userId != null) {
                        authViewModel.getUserRole(userId) { role, isBlocked ->
                            if (role != null) {
                                AppNavigator.navigateToRoleDestination(
                                    requireActivity(),
                                    role,
                                    isBlocked
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

