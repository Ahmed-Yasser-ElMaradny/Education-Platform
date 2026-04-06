package com.ahmed.ostazahmed.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ahmed.ostazahmed.AuthenticationHelper.AppNavigator
import com.ahmed.ostazahmed.AuthenticationHelper.AuthViewModel
import com.ahmed.ostazahmed.R
import com.ahmed.ostazahmed.Utils.FirebaseManager
import com.ahmed.ostazahmed.databinding.SignInFragmentBinding


class SignInFragment() : Fragment() {
    private var _binding: SignInFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = SignInFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.SignUpText.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
        val authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            authViewModel.signIn(email, password) { success, error ->
                if (success) {
                    val userId = FirebaseManager.currentUserUid
                    if (userId != null) {
                        authViewModel.getUserRole(userId) { role, isBlocked ->
                            AppNavigator.navigateToRoleDestination(
                                requireActivity(),
                                role!!,
                                isBlocked
                            )
                        }
                    } else Toast.makeText(requireContext(), "User Id is null", Toast.LENGTH_SHORT)
                        .show()
                } else Toast.makeText(
                    requireContext(),
                    "Sign in failed: $error",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.forgotPasswordText.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_forgotPasswordFragment)
        }
    }
}
