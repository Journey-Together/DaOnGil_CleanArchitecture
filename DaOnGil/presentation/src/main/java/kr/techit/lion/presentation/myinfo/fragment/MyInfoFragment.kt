package kr.techit.lion.presentation.myinfo.fragment

import android.os.Bundle
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.FragmentMyInfoBinding
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.ext.announceForAccessibility
import kr.techit.lion.presentation.ext.formatBirthday
import kr.techit.lion.presentation.ext.formatPhoneNumber
import kr.techit.lion.presentation.ext.isTallBackEnabled
import kr.techit.lion.presentation.ext.repeatOnViewStarted
import kr.techit.lion.presentation.ext.setAccessibilityText
import kr.techit.lion.presentation.main.fragment.MyInfoMainFragment.Companion.MODIFY_RESULT_CODE
import kr.techit.lion.presentation.myinfo.vm.MyInfoViewModel
import kr.techit.lion.presentation.observer.ConnectivityObserver
import kr.techit.lion.presentation.observer.NetworkConnectivityObserver

@AndroidEntryPoint
class MyInfoFragment : Fragment(R.layout.fragment_my_info) {
    private val viewModel: MyInfoViewModel by activityViewModels()
    private val connectivityObserver: ConnectivityObserver by lazy {
        NetworkConnectivityObserver(requireContext().applicationContext)
    }
    private val myInfoAnnounce = StringBuilder()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMyInfoBinding.bind(view)

        if (requireContext().isTallBackEnabled()) {
            setupAccessibility(binding)

        } else {
            binding.toolbarMyInfo.menu.clear()
            binding.backButton.performAccessibilityAction(
                AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS, null
            )
        }

        binding.backButton.setOnClickListener { handleBackPress() }

        repeatOnViewStarted {
            supervisorScope {
                launch { collectName(binding) }
                launch { collectPersonalInfo(binding) }
                launch { collectIceInfo(binding) }
                launch { collectNetworkState(binding) }
                launch { observeConnectivity() }
            }
        }

        binding.btnPersonalInfoModify.setOnClickListener {
            findNavController().navigate(R.id.action_myInfoFragment_to_personalInfoModifyFragment)
        }
        binding.bntIceModify.setOnClickListener {
            findNavController().navigate(R.id.action_myInfoFragment_to_iceModifyFragment)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackPress()
        }
    }

    private suspend fun observeConnectivity(){
        connectivityObserver.getFlow().collect {
            connectivityObserver.getFlow().collect { status ->
                if (status == ConnectivityObserver.Status.Available) {
                    viewModel.initUiData()
                }
            }
        }
    }

    private fun setupAccessibility(binding: FragmentMyInfoBinding) {
        requireActivity().announceForAccessibility(
            getString(R.string.text_script_guide_for_my_info) +
                    getString(R.string.text_script_read_all_text)
        )
        myInfoAnnounce.append(getString(R.string.text_personal_info))

        binding.toolbarMyInfo.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.read_script -> {
                    requireActivity().announceForAccessibility(getString(R.string.text_script_my_info))
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

    private suspend fun collectName(binding: FragmentMyInfoBinding) {
        viewModel.name.filter { it.isNotEmpty() }.collect {
            binding.tvName.text = it
            if (requireContext().isTallBackEnabled()){
                binding.tvNameTitle.setAccessibilityText("${ binding.tvNameTitle.text } $it")
                binding.tvName.setAccessibilityText("${ binding.tvNameTitle.text } $it")
            }
        }
    }

    private suspend fun collectPersonalInfo(binding: FragmentMyInfoBinding) {
        viewModel.myPersonalInfo.collect {
            with(binding) {
                tvNickname.text = it.nickname
                tvPhone.text = it.phone

                if (requireContext().isTallBackEnabled()){
                    tvPhoneTitle.setAccessibilityText(
                        if (it.phone.isEmpty()) "${tvPhoneTitle.text} ${getString(R.string.text_plz_enter_phone)}"
                        else "${tvPhoneTitle.text} ${it.phone.formatPhoneNumber()}"
                    )
                    tvPhone.setAccessibilityText(
                        if (it.phone.isEmpty()) "${tvPhoneTitle.text} ${getString(R.string.text_plz_enter_phone)}"
                        else "${tvPhoneTitle.text} ${it.phone.formatPhoneNumber()}"
                    )
                    tvNicknameTitle.setAccessibilityText(
                        if (it.nickname.isEmpty()) "${tvNicknameTitle.text} ${getString(R.string.text_plz_enter_nickname)}"
                        else "${tvNicknameTitle.text} ${it.nickname}"
                    )
                    tvNickname.setAccessibilityText(
                        if (it.nickname.isEmpty()) "${tvNicknameTitle.text} ${getString(R.string.text_plz_enter_nickname)}"
                        else "${tvNicknameTitle.text} ${it.nickname}"
                    )
                }
            }
        }
    }

    private suspend fun collectIceInfo(binding: FragmentMyInfoBinding) {
        viewModel.myIceInfo.collect {
            with(binding) {
                tvBirth.text = it.birth
                tvBloodType.text = it.bloodType
                tvDisease.text = it.disease
                tvAllergy.text = it.allergy
                tvMedicine.text = it.medication
                tvRelation1.text = it.part1Rel
                tvContact1.text = it.part1Phone
                tvRelation2.text = it.part2Rel
                tvContact2.text = it.part2Phone

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
                    tvBloodType.setAccessibilityText(
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
                }
            }
        }
    }

    private suspend fun collectNetworkState(binding: FragmentMyInfoBinding) {
        with(binding) {
            viewModel.networkState.collect {
                when (it) {
                    is NetworkState.Loading -> progressBar.visibility = View.VISIBLE
                    is NetworkState.Success -> {
                        progressBar.visibility = View.GONE
                        errorContainer.visibility = View.GONE
                        mainContainer.visibility = View.VISIBLE
                        if (requireContext().isTallBackEnabled()) {
                            buildAccessibilityAnnouncement(binding)
                        }
                    }
                    is NetworkState.Error -> {
                        progressBar.visibility = View.GONE
                        mainContainer.visibility = View.GONE
                        errorContainer.visibility = View.VISIBLE
                        textMsg.text = it.msg
                        if (requireContext().isTallBackEnabled()) {
                            requireActivity().announceForAccessibility(it.msg)
                        }
                    }
                }
            }
        }
    }

    private fun buildAccessibilityAnnouncement(binding: FragmentMyInfoBinding) {
        with(binding) {
            myInfoAnnounce
                .append(getString(R.string.text_name)).append(tvName.text)
                .append(getString(R.string.text_nickname)).append(tvNickname.text)
                .append(getString(R.string.text_phone)).append(tvPhone.text.toString().formatPhoneNumber())
                .append(getString(R.string.text_birth)).append(tvBirth.text.toString().formatBirthday())
                .append(getString(R.string.text_blood_type)).append(tvBloodType.text)
                .append(getString(R.string.text_disease)).append(tvDisease.text)
                .append(getString(R.string.text_allergy)).append(tvAllergy.text)
                .append(getString(R.string.text_medicine)).append(tvMedicine.text)
                .append(getString(R.string.text_emergency_contact))
                .append(tvRelation1.text).append(tvContact1.text.toString().formatPhoneNumber())
                .append(tvRelation2.text).append(tvContact2.text.toString().formatPhoneNumber())
        }
    }

    private fun handleBackPress() {
        if (viewModel.isPersonalInfoModified.value) {
            requireActivity().setResult(MODIFY_RESULT_CODE)
        }
        requireActivity().finish()
    }
}