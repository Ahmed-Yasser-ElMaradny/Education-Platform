package com.ahmed.ostazahmed.TeacherFragments.generator

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmed.ostazahmed.Domain.AccessCode
import com.ahmed.ostazahmed.TeacherRecyclerViews.TeacherGeneratedCodesAdapter
import com.ahmed.ostazahmed.databinding.FragmentGenerateCodesBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class GenerateCodesFragment : Fragment() {

    private var _binding: FragmentGenerateCodesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter: TeacherGeneratedCodesAdapter
    private val fireBaseDatabase = Firebase.firestore
    private val fireBaseUser = Firebase.auth


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentGenerateCodesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }



    @SuppressLint("DefaultLocale")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val generateCodesViewModel =
            ViewModelProvider(this)[GenerateCodesViewModel::class.java]

        val factory = StudentsCodesViewModelFactory(requireContext())

        val studentCodesToPdfViewModel =
            ViewModelProvider(this , factory)[StudentCodesToPdfViewModel::class.java]
        generateCodesViewModel.codeValidation()

        adapter = TeacherGeneratedCodesAdapter(
            onCodeCopied = { copyCode(it) },
            onDelete = { deleteCode(it) }
        )


        binding.rvCodes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCodes.adapter = adapter

        binding.btnDeleteAll.setOnClickListener {

                generateCodesViewModel.deleteAllCodes()
                binding.emptyState.visibility = View.VISIBLE

            }



        binding.progressCodes.visibility = View.VISIBLE // أظهر التحميل
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 2. استخدام submitList بدل updateData
                generateCodesViewModel.codes.collect { codesList ->
                    binding.progressCodes.visibility = View.GONE
                    adapter.submitList(codesList)
                }
            }
        }


        binding.etExpiryDays.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val dataPicker =
                DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = String.format(
                        "%04d/%02d/%02d",
                        selectedYear,
                        selectedMonth + 1,
                        selectedDay
                    )
                    binding.etExpiryDays.setText(formattedDate)
                }, year, month, day)

            dataPicker.show()
        }

        binding.btnGenerate.setOnClickListener {
            val count = binding.etCount.text.toString().toIntOrNull() ?: 0
            val credits = binding.etCredits.text.toString().toIntOrNull() ?: 0
            val expiryDays = binding.etExpiryDays.text.toString()


            val errorMessage = when {
                count.toString().isEmpty() ->
                    "قم بوضع عدد الأكواد المراد توليدها"

                credits.toString().isEmpty() ->
                    "قم بوضع عدد الكريديتس للأكواد"

                expiryDays.isEmpty() ->
                    "قم بوضع تاريخ انتهاء الاكواد"


                else -> null
            }

            if (errorMessage != null) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.progressCodes.visibility = View.VISIBLE
            binding.emptyState.visibility = View.GONE

            repeat(count) {
                val code = generatingCode(4) + '-' + generatingCode(4) + '-' + generatingCode(4)
                val accessCode = AccessCode(
                    code,
                    credits,
                    false,
                    "",
                    "",
                    expiryDays,
                    convertDate(),
                    fireBaseUser.currentUser?.uid
                )

                    generateCodesViewModel.addCodes(accessCode)

            }
            binding.progressCodes.visibility = View.GONE
            binding.emptyState.visibility = View.GONE

        }

        binding.btnMakePdf.setOnClickListener {
            val currentCodes = generateCodesViewModel.codes.value ?: emptyList()

            // التريكة هنا: نختبر اللستة الأول
            if (currentCodes.isEmpty()) {
                Toast.makeText(requireContext(), "مفيش أكواد متولدة لسه يا هندسة!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // يوقف الكود وميكملش
            }

            // لو اللستة مليانة، نطلع رسالة إننا بدأنا، وبعدين نبعت للـ ViewModel
            Toast.makeText(requireContext(), "جاري تجهيز الملف... ${currentCodes.size} كود", Toast.LENGTH_SHORT).show()
            studentCodesToPdfViewModel.generatePdfForStudents(currentCodes.map { it.code })
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                studentCodesToPdfViewModel.pdfGenerationStatus.collect { isSuccess ->
                    when (isSuccess) {
                        true -> {
                            Toast.makeText(requireContext(), "تم حفظ الـ PDF بنجاح في المستندات!", Toast.LENGTH_LONG).show()
                        }
                        false -> {
                            Toast.makeText(requireContext(), "حصل خطأ أثناء الحفظ! راجع الـ Logcat.", Toast.LENGTH_LONG).show()
                        }
                        null -> { /* دي الحالة المبدئية مش بنعمل فيها حاجة */ }
                    }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun generatingCode(length: Int): String {
        val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val random = SecureRandom()
        val code = StringBuilder()

        repeat(length) {
            val index = random.nextInt(chars.size)
            code.append(chars[index])
        }
        return code.toString()
    }

    private fun convertDate(): String {
        val timeInMillis = System.currentTimeMillis()
        val date = Date(timeInMillis)
        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val formatterDate = formatter.format(date)
        return formatterDate
    }

    private fun deleteCode(accessCode: AccessCode){
        fireBaseDatabase.collection("codes").document(accessCode.code).delete()
    }

    private fun copyCode(accessCode: AccessCode){

        val copiedCode = accessCode.code
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Code" , copiedCode)
        clipboard.setPrimaryClip(clipData)
    }

}



