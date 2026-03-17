package com.ahmed.ostazahmed

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ahmed.ostazahmed.StudentRecyclerViewsAndRepo.StudentViewModel
import com.ahmed.ostazahmed.databinding.ActivityStudentCodeBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class StudentCodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudentCodeBinding
    private val fireBaseDataBase = Firebase.firestore

    private val fireBaseAuth = FirebaseAuth.getInstance()
    private val fireBaseUser = fireBaseAuth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStudentCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        binding.btnRedeem.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnRedeem.isEnabled = false
            val code = binding.etCode.text.toString()

            if (code.isEmpty()) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Please Enter Code", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            studentViewModel.onRedeemClicked(
                code,
                onSuccess = { task ->
                    binding.progressBar.visibility = View.GONE
                    binding.btnRedeem.isEnabled = true
                    Toast.makeText(this, task, Toast.LENGTH_SHORT).show()
                },
                onError = { exception, task ->
                    Toast.makeText(this, task, Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.btnSignOut.setOnClickListener {
            fireBaseAuth.signOut()
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

    }
}