package kr.techit.lion.presentation.myinfo.fragment

import android.os.Bundle
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
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
import kr.techit.lion.presentation.myinfo.intent.MyInfoIntent
import kr.techit.lion.presentation.myinfo.vm.MyInfoViewModel
import kr.techit.lion.presentation.observer.ConnectivityObserver
import kr.techit.lion.presentation.observer.NetworkConnectivityObserver
import kr.techit.lion.domain.model.PersonalInfo
import kr.techit.lion.domain.model.IceInfo

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
        initializeAccessibility(binding)
        initializeListener(binding)

        repeatOnViewStarted {
            supervisorScope {
                launch { collectPersonalInfo(binding) }
                launch { collectNetworkState(binding) }
                launch { observeConnectivity() }
            }
        }
    }

    private fun initializeListener(binding: FragmentMyInfoBinding) {
        binding.backButton.setOnClickListener { handleBackPress() }
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

    private fun initializeAccessibility(binding: FragmentMyInfoBinding) {
        if (requireContext().isTallBackEnabled()) {
            setupAccessibility(binding)
        } else {
            binding.toolbarMyInfo.menu.clear()
            binding.backButton.performAccessibilityAction(
                AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS, null
            )
        }
    }

    private suspend fun observeConnectivity() {
        connectivityObserver.getFlow().collect { status ->
            if (status == ConnectivityObserver.Status.Available) {
                viewModel.onChangeUiEvent(MyInfoIntent.OnUiEventInitializeUiData)
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

    private suspend fun collectPersonalInfo(binding: FragmentMyInfoBinding) {
        viewModel.state.collect { state ->
            initializedPersonalInfoUiData(binding, state.personalInfo)
            initializedIceInfoUiData(binding, state.iceInfo)
        }
    }

    private fun initializedPersonalInfoUiData(
        binding: FragmentMyInfoBinding,
        myInfo: PersonalInfo
    ) {
        with(binding) {
            tvName.text = myInfo.userName
            tvNickname.text = myInfo.nickname
            tvPhone.text = myInfo.phone

            if (requireContext().isTallBackEnabled()) setPersonalInfoTallBack(binding, myInfo)
        }
    }

    private fun setPersonalInfoTallBack(binding: FragmentMyInfoBinding, myInfo: PersonalInfo) {
        with(binding) {
            tvNameTitle.setAccessibilityText(
                if (myInfo.userName.isEmpty()) "${tvNameTitle.text} ${getString(R.string.text_plz_enter_name)}"
                else "${tvNameTitle.text} ${myInfo.userName}"
            )
            tvPhoneTitle.setAccessibilityText(
                if (myInfo.phone.isEmpty()) "${tvPhoneTitle.text} ${getString(R.string.text_plz_enter_phone)}"
                else "${tvPhoneTitle.text} ${myInfo.phone.formatPhoneNumber()}"
            )
            tvPhone.setAccessibilityText(
                if (myInfo.phone.isEmpty()) "${tvPhoneTitle.text} ${getString(R.string.text_plz_enter_phone)}"
                else "${tvPhoneTitle.text} ${myInfo.phone.formatPhoneNumber()}"
            )
            tvNicknameTitle.setAccessibilityText(
                if (myInfo.nickname.isEmpty()) "${tvNicknameTitle.text} ${getString(R.string.text_plz_enter_nickname)}"
                else "${tvNicknameTitle.text} ${myInfo.nickname}"
            )
            tvNickname.setAccessibilityText(
                if (myInfo.nickname.isEmpty()) "${tvNicknameTitle.text} ${getString(R.string.text_plz_enter_nickname)}"
                else "${tvNicknameTitle.text} ${myInfo.nickname}"
            )
        }
    }


    private fun initializedIceInfoUiData(binding: FragmentMyInfoBinding, myInfo: IceInfo) {
        with(binding) {
            tvBirth.text = myInfo.birth
            tvBloodType.text = myInfo.bloodType
            tvDisease.text = myInfo.disease
            tvAllergy.text = myInfo.allergy
            tvMedicine.text = myInfo.medication
            tvRelation1.text = myInfo.part1Rel
            tvContact1.text = myInfo.part1Phone
            tvRelation2.text = myInfo.part2Rel
            tvContact2.text = myInfo.part2Phone

            if (requireContext().isTallBackEnabled()) {
                setIceInfoTallBack(binding, myInfo)
            }
        }
    }

    private fun setIceInfoTallBack(binding: FragmentMyInfoBinding, myInfo: IceInfo){
        with(binding) {
            tvBirthTitle.setAccessibilityText(
                if (myInfo.birth.isEmpty()) "${tvBirthTitle.text} ${getString(R.string.text_plz_enter_birth)}"
                else "${tvBirthTitle.text} ${myInfo.birth.formatBirthday()}"
            )
            tvBirth.setAccessibilityText(
                if (myInfo.birth.isEmpty()) "${tvBirthTitle.text} ${getString(R.string.text_plz_enter_birth)}"
                else "${tvBirthTitle.text} ${myInfo.birth.formatBirthday()}"
            )
            tvBloodTypeTitle.setAccessibilityText(
                if (myInfo.bloodType.isEmpty()) "${tvBloodTypeTitle.text} ${getString(R.string.text_plz_enter_blood_type)}"
                else "${tvBloodTypeTitle.text} ${myInfo.bloodType}"
            )
            tvBloodType.setAccessibilityText(
                if (myInfo.bloodType.isEmpty()) "${tvBloodTypeTitle.text} ${getString(R.string.text_plz_enter_blood_type)}"
                else "${tvBloodTypeTitle.text} ${myInfo.bloodType}"
            )
            tvDiseaseTitle.setAccessibilityText(
                if (myInfo.disease.isEmpty()) "${tvDiseaseTitle.text} ${getString(R.string.text_plz_enter_disease)}"
                else "${tvDiseaseTitle.text} ${myInfo.disease}}"
            )
            tvDisease.setAccessibilityText(
                if (myInfo.disease.isEmpty()) "${tvDiseaseTitle.text} ${getString(R.string.text_plz_enter_disease)}"
                else "${tvDiseaseTitle.text} ${myInfo.disease}}"
            )
            tvAllergyTitle.setAccessibilityText(
                if (myInfo.allergy.isEmpty()) "${tvAllergyTitle.text} ${getString(R.string.text_plz_enter_allergy)}"
                else "${tvAllergyTitle.text} ${myInfo.allergy}"
            )
            tvAllergy.setAccessibilityText(
                if (myInfo.allergy.isEmpty()) "${tvAllergyTitle.text} ${getString(R.string.text_plz_enter_allergy)}"
                else "${tvAllergyTitle.text} ${myInfo.allergy}"
            )
            tvMedicineTitle.setAccessibilityText(
                if (myInfo.medication.isEmpty()) "${tvMedicineTitle.text} ${getString(R.string.text_plz_enter_medicine)}"
                else "${tvMedicineTitle.text} ${myInfo.medication}"
            )
            tvMedicine.setAccessibilityText(
                if (myInfo.medication.isEmpty()) "${tvMedicineTitle.text} ${getString(R.string.text_plz_enter_medicine)}"
                else "${tvMedicineTitle.text} ${myInfo.medication}"
            )
            tvRelation1.setAccessibilityText(
                if (myInfo.part1Rel.isEmpty()) getString(R.string.text_plz_enter_relation)
                else "${myInfo.part1Rel} ${myInfo.part1Phone.formatPhoneNumber()}"
            )
            tvContact1.setAccessibilityText(
                if (myInfo.part1Rel.isEmpty()) getString(R.string.text_plz_enter_relation)
                else "${myInfo.part1Rel} ${myInfo.part1Phone.formatPhoneNumber()}"
            )
            tvRelation2.setAccessibilityText(
                if (myInfo.part2Rel.isEmpty()) getString(R.string.text_plz_enter_relation)
                else "${myInfo.part2Rel} ${myInfo.part2Phone.formatPhoneNumber()}"
            )
            tvContact2.setAccessibilityText(
                if (myInfo.part2Rel.isEmpty()) getString(R.string.text_plz_enter_relation)
                else "${myInfo.part2Rel} ${myInfo.part2Phone.formatPhoneNumber()}"
            )
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
                .append(getString(R.string.text_phone))
                .append(tvPhone.text.toString().formatPhoneNumber())
                .append(getString(R.string.text_birth))
                .append(tvBirth.text.toString().formatBirthday())
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
        if (viewModel.state.value.isPersonalInfoModified) {
            requireActivity().setResult(MODIFY_RESULT_CODE)
        }
        requireActivity().finish()
    }
}