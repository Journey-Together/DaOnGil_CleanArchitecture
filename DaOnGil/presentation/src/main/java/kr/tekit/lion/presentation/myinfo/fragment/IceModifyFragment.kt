package kr.tekit.lion.presentation.myinfo.fragment

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentIceModifyBinding
import kr.tekit.lion.presentation.ext.repeatOnViewStarted
import kr.tekit.lion.presentation.ext.showSoftInput
import kr.tekit.lion.presentation.myinfo.vm.MyInfoViewModel

@AndroidEntryPoint
class IceModifyFragment : Fragment(R.layout.fragment_ice_modify) {

    private val viewModel: MyInfoViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentIceModifyBinding.bind(view)
        with(binding) {

            toolbarIceModify.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            buttonIceSubmit.setOnClickListener {
                if (isFormValid(binding)) {
                    showSnackbar(binding, "나의 응급 정보가 수정 되었습니다.")
//                    viewModel.onCompleteModifyIce(
//                        IceInfo(
//                            bloodType = tvBloodType.text.toString(),
//                            birth = tvBirthday.text.toString(),
//                            disease = tvDisease.text.toString(),
//                            allergy = tvAllergy.text.toString(),
//                            medication = tvMedicine.text.toString(),
//                            part1Rel = tvRelation1.text.toString(),
//                            part1Phone = tvContact1.text.toString(),
//                            part2Rel = tvRelation2.text.toString(),
//                            part2Phone = tvContact2.text.toString()
//                        )
//                    )
                    findNavController().popBackStack()
                }
            }

            initTextField(binding)
            handleTextFieldEditorActions(binding)
        }
    }

    private fun initTextField(binding: FragmentIceModifyBinding) {
        val bloodType = resources.getStringArray(R.array.blood_type)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item_blood_type, bloodType)

        with(binding.tvBloodType) {
            setDropDownBackgroundResource(R.color.background_color)
            setAdapter(arrayAdapter)

            setOnClickListener {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(it.windowToken, 0)
                showDropDown()
            }
        }

        repeatOnViewStarted {
//            with(binding) {
//                viewModel.myIceInfo.collect {
//                    tvBirthday.setText(it.birth)
//                    tvBloodType.setText(it.bloodType)
//                    tvDisease.setText(it.disease)
//                    tvAllergy.setText(it.allergy)
//                    tvMedicine.setText(it.medication)
//                    tvRelation1.setText(it.part1Rel)
//                    tvContact1.setText(it.part1Phone)
//                    tvRelation2.setText(it.part2Rel)
//                    tvContact2.setText(it.part2Phone)
//                }
//            }
        }
    }

    private fun handleTextFieldEditorActions(binding: FragmentIceModifyBinding) {
        with(binding) {
            tvBirthday.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                    event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN
                ) {

                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)

                    tvBirthday.clearFocus()

                    true
                } else {
                    false
                }
            }

            tvRelation1.setOnEditorActionListener { v, actionId, event ->
                tvContact1.requestFocus()
                true
            }

            tvContact1.setOnEditorActionListener { v, actionId, event ->
                tvContact2.requestFocus()
                true
            }

            with(tvRelation2) {
                imeOptions = EditorInfo.IME_ACTION_NEXT
                setOnEditorActionListener { v, actionId, event ->
                    tvContact2.requestFocus()
                    true
                }
            }
        }
    }

    private fun showSnackbar(binding: FragmentIceModifyBinding, message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            .show()
    }

    private fun isFormValid(binding: FragmentIceModifyBinding): Boolean {
        with(binding) {
            var isValid = true

            var firstInvalidField: View? = null
            val birthday = tvBirthday.text.toString()
            if (birthday.isNotBlank()) {
                val birthdayPattern = "^\\d{4}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])$"
                if (!birthday.matches(birthdayPattern.toRegex())) {
                    textInputLayoutBirthday.error = "올바른 생년월일 형식을 입력해주세요.\n예: 19700101"
                    firstInvalidField = tvBirthday
                    isValid = false
                }
            }

            val phoneNumber1 = tvContact1.text.toString()
            val relation1 = tvRelation1.text.toString()

            if ((relation1.isEmpty() and phoneNumber1.isNotEmpty())) {
                tvContact1.error = "관계를 입력해 주세요."
                if (firstInvalidField == null) {
                    firstInvalidField = tvRelation1
                }
                isValid = false
            } else if ((relation1.isNotEmpty() and phoneNumber1.isEmpty())) {
                tvContact1.error = "연락처를 입력해 주세요."
                if (firstInvalidField == null) {
                    firstInvalidField = tvContact1
                }
                isValid = false
            }else if((relation1.isNotEmpty()) and (phoneNumber1.isNotEmpty())){
                val phonePattern = "^010\\d{4}\\d{4}$"
                if (!phoneNumber1.matches(phonePattern.toRegex())) {
                    tvContact2.requestFocus()
                    context?.showSoftInput(tvContact2)
                    tvContact1.error = "올바른 전화번호 형식을 입력해주세요.\n예: 01012345678"
                    isValid = false
                }
            }

            val phoneNumber2 = tvContact2.text.toString()
            val relation2 = tvRelation2.text.toString()

            if ((relation2.isEmpty() and phoneNumber2.isNotEmpty())) {
                tvContact1.error = "관계를 입력해 주세요."
                if (firstInvalidField == null) {
                    firstInvalidField = tvRelation2
                }
                isValid = false
            } else if ((relation2.isNotEmpty() and phoneNumber2.isEmpty())) {
                tvContact1.error = "연락처를 입력해 주세요."
                if (firstInvalidField == null) {
                    firstInvalidField = tvRelation2
                }
                isValid = false
            } else if((relation2.isBlank()) and (phoneNumber2.isNotBlank())) {
                val phonePattern = "^010\\d{4}\\d{4}$"
                if (!phoneNumber2.matches(phonePattern.toRegex())) {
                    tvContact2.requestFocus()
                    context?.showSoftInput(tvContact2)
                    tvContact1.error =
                        "올바른 전화번호 형식을 입력해주세요.\n예: 01012345678"
                    isValid = false
                }
            }

            if (!isValid && firstInvalidField != null) {
                firstInvalidField.requestFocus()
                context?.showSoftInput(firstInvalidField)
            }
            return isValid
        }
    }
}