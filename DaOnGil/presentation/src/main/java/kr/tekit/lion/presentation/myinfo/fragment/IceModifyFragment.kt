package kr.tekit.lion.presentation.myinfo.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.model.IceInfo
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentIceModifyBinding
import kr.tekit.lion.presentation.ext.announceForAccessibility
import kr.tekit.lion.presentation.ext.formatBirthday
import kr.tekit.lion.presentation.ext.formatPhoneNumber
import kr.tekit.lion.presentation.ext.isBirthdayValid
import kr.tekit.lion.presentation.ext.isPhoneNumberValid
import kr.tekit.lion.presentation.ext.isTallBackEnabled
import kr.tekit.lion.presentation.ext.pronounceEachCharacter
import kr.tekit.lion.presentation.ext.repeatOnViewStarted
import kr.tekit.lion.presentation.ext.setAccessibilityText
import kr.tekit.lion.presentation.ext.showSoftInput
import kr.tekit.lion.presentation.myinfo.vm.MyInfoViewModel


@AndroidEntryPoint
class IceModifyFragment : Fragment(R.layout.fragment_ice_modify) {

    private val viewModel: MyInfoViewModel by activityViewModels()
    private val myInfoAnnounce = StringBuilder()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentIceModifyBinding.bind(view)

        if (requireContext().isTallBackEnabled()) setupAccessibility(binding)
        else binding.toolbarIceModify.menu.clear()

        with(binding) {
            repeatOnViewStarted {
                collectMyInfo(this@with)
            }

            backButton.setOnClickListener {
                findNavController().popBackStack()
            }

            buttonIceSubmit.setOnClickListener {
                if (isFormValid(this@with)) {
                    showSnackbar(this@with, "나의 응급 정보가 수정 되었습니다.")
                    viewModel.onCompleteModifyIce(
                        IceInfo(
                            birth = tvBirth.text.toString(),
                            disease = tvDisease.text.toString(),
                            allergy = tvAllergy.text.toString(),
                            medication = tvMedicine.text.toString(),
                            part1Rel = tvRelation1.text.toString(),
                            part1Phone = tvContact1.text.toString(),
                            part2Rel = tvRelation2.text.toString(),
                            part2Phone = tvContact2.text.toString()
                        )
                    )
                    findNavController().popBackStack()
                }
            }

            initTextField(this@with)
            handleTextFieldEditorActions(binding)
        }
    }

    private fun initTextField(binding: FragmentIceModifyBinding) {
        val bloodType = resources.getStringArray(R.array.blood_type).map { it.pronounceEachCharacter() }
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item_blood_type, bloodType)

        repeatOnViewStarted {
            with(binding) {
                tvBloodType.setAdapter(arrayAdapter)
                tvBloodType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedBloodType = parent?.getItemAtPosition(position).toString()
                        (parent?.getChildAt(0) as TextView).setTextColor(Color.BLUE)
                        viewModel.onSelectBloodType(selectedBloodType)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

                if (requireContext().isTallBackEnabled()) {
                    tvBirth.doAfterTextChanged {
                        if (it.isNullOrBlank()) tvBirth.setAccessibilityText(getString(R.string.text_plz_enter_birth))
                        else tvBirth.setAccessibilityText(it.toString().formatBirthday())
                    }

                    tvDisease.doAfterTextChanged {
                        if (it.isNullOrBlank()) tvDisease.setAccessibilityText(getString(R.string.text_plz_enter_disease))
                        else tvDisease.setAccessibilityText(it)
                    }

                    tvAllergy.doAfterTextChanged {
                        if (it.isNullOrBlank()) tvAllergy.setAccessibilityText(getString(R.string.text_plz_enter_allergy))
                        else tvAllergy.setAccessibilityText(it)
                    }

                    tvMedicine.doAfterTextChanged {
                        if (it.isNullOrBlank()) tvMedicine.setAccessibilityText(getString(R.string.text_plz_enter_medicine))
                        else tvMedicine.setAccessibilityText(it)
                    }

                    tvRelation1.doAfterTextChanged {
                        if (it.isNullOrBlank()) tvRelation1.setAccessibilityText(getString(R.string.text_plz_enter_relation))
                        else tvRelation1.setAccessibilityText(it)
                    }

                    tvContact1.doAfterTextChanged {
                        if (it.isNullOrBlank()) tvContact1.setAccessibilityText(getString(R.string.text_plz_enter_emergency_contact))
                        else tvContact1.setAccessibilityText(it.toString().formatPhoneNumber())
                    }

                    tvRelation2.doAfterTextChanged {
                        if (it.isNullOrBlank()) tvRelation2.setAccessibilityText(getString(R.string.text_plz_enter_relation))
                        else tvRelation2.setAccessibilityText(it)
                    }

                    tvContact2.doAfterTextChanged {
                        if (it.isNullOrBlank()) tvContact2.setAccessibilityText(getString(R.string.text_plz_enter_emergency_contact))
                        else tvContact2.setAccessibilityText(it.toString().formatPhoneNumber())
                    }
                }
            }
        }
    }

    private suspend fun collectMyInfo(binding: FragmentIceModifyBinding) {
        val bloodType = resources.getStringArray(R.array.blood_type).map { it.pronounceEachCharacter() }

        with(binding) {
            viewModel.myIceInfo.collect {
                val bloodTypeIndex = bloodType.indexOf(it.bloodType)
                if (bloodTypeIndex != -1) {
                    binding.tvBloodType.setSelection(bloodTypeIndex)
                }
                tvBirth.setText(it.birth)
                tvDisease.setText(it.disease)
                tvAllergy.setText(it.allergy)
                tvMedicine.setText(it.medication)
                tvRelation1.setText(it.part1Rel)
                tvContact1.setText(it.part1Phone)
                tvRelation2.setText(it.part2Rel)
                tvContact2.setText(it.part2Phone)

                if (requireContext().isTallBackEnabled()) {

                    tvBirthTitle.setAccessibilityText(
                        if (it.birth.isEmpty()) "${tvBirthTitle.text} ${getString(R.string.text_plz_enter_birth)}"
                        else "${tvBirthTitle.text} ${it.birth.formatBirthday()}"
                    )

                    tvBirth.setAccessibilityText(
                        if (it.birth.isEmpty()) "${tvBirthTitle.text} ${getString(R.string.text_plz_enter_birth)}"
                        else "${tvBirthTitle.text} ${it.birth.formatBirthday()}"
                    )

                    tvBloodTypeTitle.setAccessibilityText(
                        if (it.bloodType.isEmpty()) "${tvBloodTypeTitle.text} ${getString(R.string.text_plz_enter_blood_type)}"
                        else "${tvBloodTypeTitle.text} ${it.bloodType}"
                    )

                    tvDiseaseTitle.setAccessibilityText(
                        if (it.disease.isEmpty()) "${tvDiseaseTitle.text} ${getString(R.string.text_plz_enter_disease)}"
                        else "${tvDiseaseTitle.text} ${it.disease}}"
                    )

                    tvDisease.setAccessibilityText(
                        if (it.disease.isEmpty()) "${tvDiseaseTitle.text} ${getString(R.string.text_plz_enter_disease)}"
                        else "${tvDiseaseTitle.text} ${it.disease}}"
                    )

                    tvAllergyTitle.setAccessibilityText(
                        if (it.allergy.isEmpty()) "${tvAllergyTitle.text} ${getString(R.string.text_plz_enter_allergy)}"
                        else "${tvAllergyTitle.text} ${it.allergy}"
                    )

                    tvAllergy.setAccessibilityText(
                        if (it.allergy.isEmpty()) "${tvAllergyTitle.text} ${getString(R.string.text_plz_enter_allergy)}"
                        else "${tvAllergyTitle.text} ${it.allergy}"
                    )

                    tvMedicineTitle.setAccessibilityText(
                        if (it.medication.isEmpty()) "${tvMedicineTitle.text} ${getString(R.string.text_plz_enter_medicine)}"
                        else "${tvMedicineTitle.text} ${it.medication}"
                    )
                    tvMedicine.setAccessibilityText(
                        if (it.medication.isEmpty()) "${tvMedicineTitle.text} ${getString(R.string.text_plz_enter_medicine)}"
                        else "${tvMedicineTitle.text} ${it.medication}"
                    )

                    tvRelation1.setAccessibilityText(
                        if (it.part1Rel.isEmpty()) getString(R.string.text_plz_enter_relation)
                        else "${it.part1Rel} ${it.part1Phone.formatPhoneNumber()}"
                    )

                    tvContact1.setAccessibilityText(
                        if (it.part1Rel.isEmpty()) getString(R.string.text_plz_enter_relation)
                        else "${it.part1Rel} ${it.part1Phone.formatPhoneNumber()}"
                    )
                    tvRelation2.setAccessibilityText(
                        if (it.part2Rel.isEmpty()) getString(R.string.text_plz_enter_relation)
                        else "${it.part2Rel} ${it.part2Phone.formatPhoneNumber()}"
                    )
                    tvContact2.setAccessibilityText(
                        if (it.part2Rel.isEmpty()) getString(R.string.text_plz_enter_relation)
                        else "${it.part2Rel} ${it.part2Phone.formatPhoneNumber()}"
                    )

                    myInfoAnnounce.append(getString(R.string.text_birth))
                    myInfoAnnounce.append(
                        if (it.birth.isEmpty()) getString(R.string.text_plz_enter_birth)
                        else it.birth.formatBirthday()
                    )
                    myInfoAnnounce.append(getString(R.string.text_blood_type))
                    myInfoAnnounce.append(
                        if (it.bloodType.isEmpty()) getString(R.string.text_plz_enter_blood_type)
                        else it.bloodType
                    )
                    myInfoAnnounce.append(getString(R.string.text_disease))
                    myInfoAnnounce.append(
                        if (it.disease.isEmpty()) getString(R.string.text_plz_enter_disease)
                        else it.disease
                    )
                    myInfoAnnounce.append(getString(R.string.text_allergy))
                    myInfoAnnounce.append(
                        if (it.allergy.isEmpty()) getString(R.string.text_plz_enter_allergy)
                        else it.allergy
                    )
                    myInfoAnnounce.append(getString(R.string.text_medicine))
                    myInfoAnnounce.append(
                        if (it.medication.isEmpty()) getString(R.string.text_plz_enter_medicine)
                        else it.medication
                    )
                    myInfoAnnounce.append(getString(R.string.text_emergency_contact))
                    myInfoAnnounce.append(
                        if (it.part1Rel.isEmpty()) getString(R.string.text_relation)
                        else it.part1Rel
                    )
                    myInfoAnnounce.append(
                        if (it.part1Phone.isEmpty()) getString(R.string.text_contact_ex)
                        else it.part1Phone.formatPhoneNumber()
                    )
                    myInfoAnnounce.append(
                        if (it.part2Rel.isEmpty()) getString(R.string.text_relation)
                        else it.part2Rel.formatPhoneNumber()
                    )
                }
            }
        }
    }

    private fun handleTextFieldEditorActions(binding: FragmentIceModifyBinding) {
        with(binding) {
            tvBirth.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                    event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN
                ) {
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                    tvBirth.clearFocus()
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

    private fun setupAccessibility(binding: FragmentIceModifyBinding) {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(3000)
            requireActivity().announceForAccessibility(
                getString(R.string.text_script_guide_for_my_info) +
                        getString(R.string.text_script_read_all_text)
            )
        }
        myInfoAnnounce.append(getString(R.string.text_personal_info))

        binding.toolbarIceModify.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.read_script -> {
                    requireActivity().announceForAccessibility(
                        getString(R.string.text_script_for_modify_my_info_main)
                    )
                    true
                }

                R.id.read_info -> {
                    requireActivity().announceForAccessibility(myInfoAnnounce.toString())
                    true
                }

                else -> false
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

            val birthday = tvBirth.text.toString()
            if (birthday.isNotBlank() && !birthday.isBirthdayValid()) {
                val errorMessage = getString(R.string.text_plz_enter_collect_birth_type) + "\n" +
                        getString(R.string.text_birth_ex)
                textInputLayoutBirthday.error = errorMessage
                firstInvalidField = tvBirth
                announceError(errorMessage)
                isValid = false
            }

            val phoneNumber1 = tvContact1.text.toString()
            val relation1 = tvRelation1.text.toString()

            if (relation1.isEmpty() && phoneNumber1.isNotEmpty()) {
                val errorMessage = getString(R.string.text_plz_enter_relation)
                tvRelation1.error = errorMessage
                if (firstInvalidField == null) {
                    firstInvalidField = tvRelation1
                }
                announceError(errorMessage)
                isValid = false
            } else if (relation1.isNotEmpty() && phoneNumber1.isEmpty()) {
                val errorMessage = getString(R.string.text_plz_enter_emergency_contact)
                tvContact1.error = errorMessage
                if (firstInvalidField == null) {
                    firstInvalidField = tvContact1
                }
                announceError(errorMessage)
                isValid = false
            } else if (relation1.isNotEmpty() && phoneNumber1.isNotEmpty() && !phoneNumber1.isPhoneNumberValid()) {
                val errorMessage = getString(R.string.text_plz_enter_collect_phone_type) + "\n" +
                        getString(R.string.text_contact_ex)
                tvContact1.error = errorMessage
                if (firstInvalidField == null) {
                    firstInvalidField = tvContact1
                }
                announceError(errorMessage)
                isValid = false
            }

            val phoneNumber2 = tvContact2.text.toString()
            val relation2 = tvRelation2.text.toString()

            if (relation2.isEmpty() && phoneNumber2.isNotEmpty()) {
                val errorMessage = getString(R.string.text_plz_enter_relation)
                tvRelation2.error = errorMessage
                if (firstInvalidField == null) {
                    firstInvalidField = tvRelation2
                }
                announceError(errorMessage)
                isValid = false
            } else if (relation2.isNotEmpty() && phoneNumber2.isEmpty()) {
                val errorMessage = getString(R.string.text_plz_enter_emergency_contact)
                tvContact2.error = errorMessage
                if (firstInvalidField == null) {
                    firstInvalidField = tvContact2
                }
                announceError(errorMessage)
                isValid = false
            } else if (relation2.isNotEmpty() && phoneNumber2.isNotEmpty() && !phoneNumber2.isPhoneNumberValid()) {
                val errorMessage = getString(R.string.text_plz_enter_collect_phone_type) + "\n" +
                        getString(R.string.text_contact_ex)
                tvContact2.error = errorMessage
                if (firstInvalidField == null) {
                    firstInvalidField = tvContact2
                }
                announceError(errorMessage)
                isValid = false
            }

            if (!isValid && firstInvalidField != null) {
                firstInvalidField.requestFocus()
                context?.showSoftInput(firstInvalidField)
            }
            return isValid
        }
    }

    private fun announceError(errorMessage: String) {
        if (requireContext().isTallBackEnabled()) {
            viewLifecycleOwner.lifecycleScope.launch {
                delay(2500)
                requireActivity().announceForAccessibility(errorMessage)
            }
        }
    }
}