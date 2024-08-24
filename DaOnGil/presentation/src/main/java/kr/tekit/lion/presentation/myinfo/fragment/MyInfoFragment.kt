package kr.tekit.lion.presentation.myinfo.fragment

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentMyInfoBinding
import kr.tekit.lion.presentation.delegate.NetworkState
import kr.tekit.lion.presentation.ext.announceForAccessibility
import kr.tekit.lion.presentation.ext.formatBirthday
import kr.tekit.lion.presentation.ext.formatPhoneNumber
import kr.tekit.lion.presentation.ext.isScreenReaderEnabled
import kr.tekit.lion.presentation.ext.repeatOnViewStarted
import kr.tekit.lion.presentation.ext.setAccessibilityText
import kr.tekit.lion.presentation.main.fragment.MyInfoMainFragment.Companion.MODIFY_RESULT_CODE
import kr.tekit.lion.presentation.myinfo.vm.MyInfoViewModel

@AndroidEntryPoint
class MyInfoFragment : Fragment(R.layout.fragment_my_info) {

    private val viewModel: MyInfoViewModel by activityViewModels()
    private val myInfoAnnounce = StringBuilder()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMyInfoBinding.bind(view)

        startShimmer(binding)

        if (requireContext().isScreenReaderEnabled()) {
            setupAccessibility(binding)
        } else {
            binding.toolbarMyInfo.menu.clear()
        }

        binding.backButton.setOnClickListener { handleBackPress() }

        repeatOnViewStarted {
            supervisorScope {
                launch { collectName(binding) }
                launch { collectPersonalInfo(binding) }
                launch { collectIceInfo(binding) }
                launch { collectNetworkState(binding) }
                launch { collectErrorMessage(binding) }
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

    private fun setupAccessibility(binding: FragmentMyInfoBinding) {
        requireActivity().announceForAccessibility(getString(R.string.text_script_guide_for_my_info))
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
        viewModel.name.collect {
            binding.tvName.text = it
        }
    }

    private suspend fun collectPersonalInfo(binding: FragmentMyInfoBinding) {
        viewModel.myPersonalInfo.collect {
            with(binding) {
                tvNickname.text = it.nickname
                tvPhone.text = it.phone

                tvPhone.setAccessibilityText(
                    if (it.phone.isEmpty()) getString(R.string.text_plz_enter_phone)
                    else it.phone.formatPhoneNumber()
                )
                tvNickname.setAccessibilityText(
                    if (it.nickname.isEmpty()) getString(R.string.text_plz_enter_nickname)
                    else it.nickname
                )
            }
        }
    }

    // 응급 정보 수집 및 UI 업데이트
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

                tvBirth.setAccessibilityText(
                    if (it.birth.isEmpty()) getString(R.string.text_plz_enter_birth)
                    else it.birth.formatBirthday()
                )
                tvBloodType.setAccessibilityText(
                    if (it.bloodType.isEmpty()) getString(R.string.text_plz_enter_blood_type)
                    else it.bloodType
                )
                tvDisease.setAccessibilityText(
                    if (it.disease.isEmpty()) getString(R.string.text_plz_enter_disease)
                    else it.disease
                )
                tvAllergy.setAccessibilityText(
                    if (it.allergy.isEmpty()) getString(R.string.text_plz_enter_allergy)
                    else it.allergy
                )
                tvMedicine.setAccessibilityText(
                    if (it.medication.isEmpty()) getString(R.string.text_plz_enter_medicine)
                    else it.medication
                )
                tvRelation1.setAccessibilityText(
                    if (it.part1Rel.isEmpty()) getString(R.string.text_plz_enter_relation)
                    else it.part1Rel
                )
                tvContact1.setAccessibilityText(
                    if (it.part1Phone.isEmpty()) getString(R.string.text_plz_enter_emergency_contact)
                    else it.part1Phone.formatPhoneNumber()
                )
                tvRelation2.setAccessibilityText(
                    if (it.part2Rel.isEmpty()) getString(R.string.text_plz_enter_relation)
                    else it.part2Rel
                )
                tvContact2.setAccessibilityText(
                    if (it.part2Phone.isEmpty()) getString(R.string.text_plz_enter_emergency_contact)
                    else it.part2Phone.formatPhoneNumber()
                )
            }
        }
    }

    private suspend fun collectNetworkState(binding: FragmentMyInfoBinding) {
        viewModel.networkState.collect {
            if (it == NetworkState.Success) {
                stopShimmer(binding)
                if (requireContext().isScreenReaderEnabled()) {
                    buildAccessibilityAnnouncement(binding)
                }
            }
        }
    }

    private suspend fun collectErrorMessage(binding: FragmentMyInfoBinding) {
        viewModel.errorMessage.collect {
            if (it != null) {
                with(binding) {
                    mainContainer.visibility = View.GONE
                    errorContainer.visibility = View.VISIBLE
                    textMsg.text = it
                }
                stopShimmer(binding)
                if (requireContext().isScreenReaderEnabled()) {
                    requireActivity().announceForAccessibility(it)
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

    private fun startShimmer(binding: FragmentMyInfoBinding) {
        with(binding) {
            shimmerFrameLayout.startShimmer()
            shimmerFrameLayout.visibility = View.VISIBLE
        }
    }

    private fun stopShimmer(binding: FragmentMyInfoBinding) {
        with(binding) {
            shimmerFrameLayout.stopShimmer()
            shimmerFrameLayout.visibility = View.GONE
            mainContainer.visibility = View.VISIBLE
        }
    }

    private fun handleBackPress() {
        if (viewModel.isPersonalInfoModified.value) {
            requireActivity().setResult(MODIFY_RESULT_CODE)
        }
        requireActivity().finish()
    }
}