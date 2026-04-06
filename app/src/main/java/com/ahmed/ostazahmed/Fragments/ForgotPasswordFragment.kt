package com.ahmed.ostazahmed.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ahmed.ostazahmed.AuthenticationHelper.AuthViewModel
import com.ahmed.ostazahmed.R
import com.ahmed.ostazahmed.databinding.ActivityForgotPasswordBinding

class ForgotPasswordFragment() : Fragment() {
    private var _binding: ActivityForgotPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityForgotPasswordBinding.inflate(inflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBackForgot.setOnClickListener {
            findNavController().navigate(R.id.action_forgotPasswordFragment_to_welcomeScreen)
        }

        val authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.btnSendResetLink.setOnClickListener {
            authViewModel.forgotPassword(binding.tietEmailForgot.text.toString()) { isSuccess, message ->

                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }


}