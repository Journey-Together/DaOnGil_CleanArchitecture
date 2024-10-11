package kr.techit.lion.presentation.myinfo.fragment

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
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
import kr.techit.lion.domain.model.IceInfo
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.FragmentIceModifyBinding
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.ext.announceForAccessibility
import kr.techit.lion.presentation.ext.formatBirthday
import kr.techit.lion.presentation.ext.formatPhoneNumber
import kr.techit.lion.presentation.ext.isBirthdayValid
import kr.techit.lion.presentation.ext.isPhoneNumberValid
import kr.techit.lion.presentation.ext.isTallBackEnabled
import kr.techit.lion.presentation.ext.pronounceEachCharacter
import kr.techit.lion.presentation.ext.repeatOnViewStarted
import kr.techit.lion.presentation.ext.setAccessibilityText
import kr.techit.lion.presentation.ext.showInfinitySnackBar
import kr.techit.lion.presentation.ext.showSoftInput
import kr.techit.lion.presentation.myinfo.vm.MyInfoViewModel
import kr.techit.lion.presentation.observer.ConnectivityObserver
import kr.techit.lion.presentation.observer.NetworkConnectivityObserver


@AndroidEntryPoint
class IceModifyFragment : Fragment(R.layout.fragment_ice_modify) {

    private val viewModel: MyInfoViewModel by activityViewModels()
    private val connectivityObserver: ConnectivityObserver by lazy {
        NetworkConnectivityObserver.getInstance(requireContext())
    }
    private val myInfoAnnounce = StringBuilder()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentIceModifyBinding.bind(view)

        if (requireContext().isTallBackEnabled()) setupAccessibility(binding)
        else binding.toolbarIceModify.menu.clear()

        repeatOnViewStarted {
            collectConnectivity(binding)
        }
        observeNetworkState(binding)

        with(binding) {
            initMyInfo(this@with)
            initTextField(this@with)
            handleTextFieldEditorActions(this@with)
        }
    }

    private fun observeNetworkState(binding: FragmentIceModifyBinding){
        with(binding) {
            viewModel.iceModifyState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is NetworkState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                    }
                    is NetworkState.Success -> {
                        progressBar.visibility = View.GONE
                        showSnackbar(this@with, "나의 응급 정보가 수정 되었습니다.")
                        findNavController().popBackStack()
                    }
                    is NetworkState.Error -> {
                        progressBar.visibility = View.GONE
                        showSnackbar(binding, state.msg)
                    }
                }
            }
        }
    }

    private suspend fun collectConnectivity(binding: FragmentIceModifyBinding) {
        with(binding) {
            connectivityObserver.getFlow().collect {
                when (it) {
                    ConnectivityObserver.Status.Available -> {
                        binding.buttonIceSubmit.isEnabled = true

                        buttonIceSubmit.setOnClickListener {
                            if (isFormValid(this@with)) {
                                viewModel.onCompleteModifyIce(
                                    IceInfo(
                                        birth = tvBirth.text.toString(),
                                        bloodType = tvBloodType.text.toString(),
                                        disease = tvDisease.text.toString(),
                                        allergy = tvAllergy.text.toString(),
                                        medication = tvMedicine.text.toString(),
                                        part1Rel = tvRelation1.text.toString(),
                                        part1Phone = tvContact1.text.toString(),
                                        part2Rel = tvRelation2.text.toString(),
                                        part2Phone = tvContact2.text.toString()
                                    )
                                )
                            }
                        }
                    }

                    ConnectivityObserver.Status.Unavailable,
                    ConnectivityObserver.Status.Losing,
                    ConnectivityObserver.Status.Lost -> {
                        binding.buttonIceSubmit.isEnabled = false
                        val msg = "${getString(R.string.text_network_is_unavailable)}\n" +
                                "${getString(R.string.text_plz_check_network)} "
                        requireContext().showInfinitySnackBar(buttonIceSubmit, msg)
                    }
                }
            }
        }
    }

    private fun initTextField(binding: FragmentIceModifyBinding) {
        val bloodType = resources.getStringArray(R.array.blood_type).map { it.pronounceEachCharacter() }
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

        with(binding) {
            if (requireContext().isTallBackEnabled()) {
                tvBirth.doAfterTextChanged {
                    if (it.isNullOrBlank()) tvBirth.setAccessibilityText(getString(R.string.text_plz_enter_birth))
                    else tvBirth.setAccessibilityText(it.toString().formatBirthday())
                }

                tvBloodType.doAfterTextChanged {
                    tvBloodTypeTitle.setAccessibilityText(
                        getString(R.string.text_blood_type) + it.toString()
                    )
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

    private fun initMyInfo(binding: FragmentIceModifyBinding) {
        with(binding) {
            backButton.setOnClickListener {
                findNavController().popBackStack()
            }

            val currentMyInfo = viewModel.myIceInfo.value
            val birthDay = currentMyInfo.birth
            val bloodType = currentMyInfo.bloodType
            val disease = currentMyInfo.disease
            val allergy = currentMyInfo.allergy
            val medication = currentMyInfo.medication
            val part1Rel = currentMyInfo.part1Rel
            val part1Phone = currentMyInfo.part1Phone
            val part2Rel = currentMyInfo.part2Rel
            val part2Phone = currentMyInfo.part2Phone

            tvBirth.setText(birthDay)
            tvBloodType.setText(bloodType)
            tvDisease.setText(disease)
            tvAllergy.setText(allergy)
            tvMedicine.setText(medication)
            tvRelation1.setText(part1Rel)
            tvContact1.setText(part1Phone)
            tvRelation2.setText(part2Rel)
            tvContact2.setText(part2Phone)

            if (requireContext().isTallBackEnabled()) {
                tvBirthTitle.setAccessibilityText(
                    if (birthDay.isEmpty()) "${tvBirthTitle.text} ${getString(R.string.text_plz_enter_birth)}"
                    else "${tvBirthTitle.text} ${birthDay.formatBirthday()}"
                )

                tvBirth.setAccessibilityText(
                    if (birthDay.isEmpty()) "${tvBirthTitle.text} ${getString(R.string.text_plz_enter_birth)}"
                    else "${tvBirthTitle.text} ${birthDay.formatBirthday()}"
                )

                tvBloodTypeTitle.setAccessibilityText(
                    if (bloodType.isEmpty()) "${tvBloodTypeTitle.text} ${getString(R.string.text_plz_enter_blood_type)}"
                    else "${tvBloodTypeTitle.text} ${bloodType}"
                )

                tvDiseaseTitle.setAccessibilityText(
                    if (disease.isEmpty()) "${tvDiseaseTitle.text} ${getString(R.string.text_plz_enter_disease)}"
                    else "${tvDiseaseTitle.text} ${disease}}"
                )

                tvDisease.setAccessibilityText(
                    if (disease.isEmpty()) "${tvDiseaseTitle.text} ${getString(R.string.text_plz_enter_disease)}"
                    else "${tvDiseaseTitle.text} ${disease}}"
                )

                tvAllergyTitle.setAccessibilityText(
                    if (allergy.isEmpty()) "${tvAllergyTitle.text} ${getString(R.string.text_plz_enter_allergy)}"
                    else "${tvAllergyTitle.text} ${allergy}"
                )

                tvAllergy.setAccessibilityText(
                    if (allergy.isEmpty()) "${tvAllergyTitle.text} ${getString(R.string.text_plz_enter_allergy)}"
                    else "${tvAllergyTitle.text} ${allergy}"
                )

                tvMedicineTitle.setAccessibilityText(
                    if (medication.isEmpty()) "${tvMedicineTitle.text} ${getString(R.string.text_plz_enter_medicine)}"
                    else "${tvMedicineTitle.text} $medication"
                )
                tvMedicine.setAccessibilityText(
                    if (medication.isEmpty()) "${tvMedicineTitle.text} ${getString(R.string.text_plz_enter_medicine)}"
                    else "${tvMedicineTitle.text} $medication"
                )

                tvRelation1.setAccessibilityText(
                    if (part1Rel.isEmpty()) getString(R.string.text_plz_enter_relation)
                    else "$part1Rel ${part1Phone.formatPhoneNumber()}"
                )

                tvContact1.setAccessibilityText(
                    if (part1Rel.isEmpty()) getString(R.string.text_plz_enter_relation)
                    else "${part1Rel} ${part1Phone.formatPhoneNumber()}"
                )
                tvRelation2.setAccessibilityText(
                    if (part2Rel.isEmpty()) getString(R.string.text_plz_enter_relation)
                    else "${part2Rel} ${part2Phone.formatPhoneNumber()}"
                )
                tvContact2.setAccessibilityText(
                    if (part2Rel.isEmpty()) getString(R.string.text_plz_enter_relation)
                    else "${part2Rel} ${part2Phone.formatPhoneNumber()}"
                )

                myInfoAnnounce.append(getString(R.string.text_birth))
                myInfoAnnounce.append(
                    if (birthDay.isEmpty()) getString(R.string.text_plz_enter_birth)
                    else birthDay.formatBirthday()
                )
                myInfoAnnounce.append(getString(R.string.text_blood_type))
                myInfoAnnounce.append(
                    if (bloodType.isEmpty()) getString(R.string.text_plz_enter_blood_type)
                    else bloodType
                )
                myInfoAnnounce.append(getString(R.string.text_disease))
                myInfoAnnounce.append(
                    if (disease.isEmpty()) getString(R.string.text_plz_enter_disease)
                    else disease
                )
                myInfoAnnounce.append(getString(R.string.text_allergy))
                myInfoAnnounce.append(
                    if (allergy.isEmpty()) getString(R.string.text_plz_enter_allergy)
                    else allergy
                )
                myInfoAnnounce.append(getString(R.string.text_medicine))
                myInfoAnnounce.append(
                    if (medication.isEmpty()) getString(R.string.text_plz_enter_medicine)
                    else medication
                )
                myInfoAnnounce.append(getString(R.string.text_emergency_contact))
                myInfoAnnounce.append(
                    if (part1Rel.isEmpty()) getString(R.string.text_relation)
                    else part1Rel
                )
                myInfoAnnounce.append(
                    if (part1Phone.isEmpty()) getString(R.string.text_contact_ex)
                    else part1Phone.formatPhoneNumber()
                )
                myInfoAnnounce.append(
                    if (part2Rel.isEmpty()) getString(R.string.text_relation)
                    else part2Rel.formatPhoneNumber()
                )
            }
        }
    }


    private fun handleTextFieldEditorActions(binding: FragmentIceModifyBinding) {
        with(binding) {
            tvBirth.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                    event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN
                ) {
                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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