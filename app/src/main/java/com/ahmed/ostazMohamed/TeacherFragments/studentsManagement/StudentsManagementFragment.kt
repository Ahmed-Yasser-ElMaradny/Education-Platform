package com.ahmed.ostazMohamed.TeacherFragments.studentsManagement

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmed.ostazMohamed.AuthActivity
import com.ahmed.ostazMohamed.TeacherRecyclerViews.ManageStudentsRecyclerView
import com.ahmed.ostazMohamed.Utils.FirebaseManager
import com.ahmed.ostazMohamed.databinding.FragmentStudentsManagementBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class StudentsManagementFragment : Fragment() {

    private var _binding: FragmentStudentsManagementBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ManageStudentsRecyclerView
    private val fireBaseDatabase = FirebaseManager.db
    private val fireBaseUser = FirebaseAuth.getInstance().currentUser
    private val fireBaseAuth = Firebase.auth


    // التعديل 1: ربط الـ ViewModel بالـ Activity عشان الداتا متتحملش كل شوية
    // لازم تضيفي dependency: implementation "androidx.fragment:fragment-ktx:1.6.2"
    private val studentsViewModel: StudentsManagementsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStudentsManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ملحوظة: شيلنا سطر ViewModelProvider(this) واستخدمنا اللي فوق

        adapter = ManageStudentsRecyclerView(
            emptyList(),
            onBlock = { user ->
                studentsViewModel.blockStudent(user, onSuccess = { task ->
                    Toast.makeText(requireContext(), task, Toast.LENGTH_SHORT).show()
                }, onError = { exception, task ->
                    Toast.makeText(requireContext(), task, Toast.LENGTH_SHORT).show()
                })
            },
            onRecover = { user ->
                studentsViewModel.recoverStudent(user, onSuccess = { task ->
                    Toast.makeText(requireContext(), task, Toast.LENGTH_SHORT).show()
                }, onError = { exception, task ->
                    Toast.makeText(requireContext(), task, Toast.LENGTH_SHORT).show()
                })
            }
        )

        binding.rvStudents.adapter = adapter
        binding.rvStudents.layoutManager = LinearLayoutManager(requireContext())

        studentsViewModel.students.observe(viewLifecycleOwner) {
            adapter.updateUsersList(it)
        }

        // التعديل 2: بنشيك لو الداتا موجودة أصلاً منحملهاش تاني
        if (studentsViewModel.students.value.isNullOrEmpty()) {
            studentsViewModel.observeStudents()
        }

        binding.btnSignOut.setOnClickListener {
            fireBaseAuth.signOut()
            val intent = Intent(requireContext(), AuthActivity::class.java)
            // مهم: امسح الـ Activity Stack عشان ميرجعش بالـ Back
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            requireActivity().finish() // السطر اللي فوق بيقوم بالواجب وزيادة
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}