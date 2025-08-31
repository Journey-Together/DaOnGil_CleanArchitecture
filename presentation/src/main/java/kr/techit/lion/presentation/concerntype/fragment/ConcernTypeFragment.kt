package kr.techit.lion.presentation.concerntype.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.techit.lion.domain.model.concern.ConcernType
import kr.techit.lion.domain.model.concern.Concerns
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.concerntype.vm.ConcernTypeViewModel
import kr.techit.lion.presentation.connectivity.ConnectivityObserver
import kr.techit.lion.presentation.databinding.FragmentConcernTypeBinding
import kr.techit.lion.presentation.delegate.NetworkEvent
import kr.techit.lion.presentation.ext.isTallBackEnabled
import kr.techit.lion.presentation.ext.repeatOnViewStarted

@AndroidEntryPoint
class ConcernTypeFragment : Fragment(R.layout.fragment_concern_type) {
    private val viewModel: ConcernTypeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentConcernTypeBinding.bind(view)

        settingToolbar(binding)
        moveConcernTypeModify(binding)

        repeatOnViewStarted {
            launch { collectConcernTypeState(binding) }
            launch { observeConnectivity(binding) }
            launch { collectUiState(binding) }
        }
    }

    private suspend fun collectUiState(binding: FragmentConcernTypeBinding) {
        viewModel.uiState.collect { uiState ->
            displayNickName(binding, uiState.nickName)
            setSelectedConcernTypeImage(binding, uiState.concernType)
        }
    }

    private suspend fun collectConcernTypeState(binding: FragmentConcernTypeBinding) {
        with(binding) {
            viewModel.networkEvent.collect { networkState ->
                when (networkState) {
                    NetworkEvent.Loading -> concernTypeProgressBar.visibility = View.VISIBLE
                    NetworkEvent.Success -> concernTypeProgressBar.visibility = View.GONE
                    is NetworkEvent.Error -> {
                        showExceptionView(binding)
                        concernTypeErrorMsg.text = networkState.msg
                        concernTypeProgressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private suspend fun observeConnectivity(binding: FragmentConcernTypeBinding) {
        with(binding) {
            viewModel.connectivityState.collect { status ->
                when (status) {
                    ConnectivityObserver.Status.Available -> {
                        concernTypeLayout.visibility = View.VISIBLE
                        concernTypeDivider.visibility = View.VISIBLE
                        concernTypeModifyLayout.visibility = View.VISIBLE
                        concernTypeErrorMsg.visibility = View.GONE
                    }

                    else -> {
                        showExceptionView(binding)
                        concernTypeErrorMsg.text =
                            requireContext().getString(R.string.can_not_access_network)
                    }
                }
            }
        }
    }

    private fun showExceptionView(binding: FragmentConcernTypeBinding) = with(binding) {
        concernTypeLayout.visibility = View.GONE
        concernTypeDivider.visibility = View.GONE
        concernTypeModifyLayout.visibility = View.GONE
        concernTypeErrorMsg.visibility = View.VISIBLE
    }

    private fun settingToolbar(binding: FragmentConcernTypeBinding) {
        with(binding.toolbarConcernType) {
            setNavigationOnClickListener {
                requireActivity().finish()
            }
            setNavigationContentDescription(R.string.text_back_button)
        }
    }

    private fun displayNickName(binding: FragmentConcernTypeBinding, nickName: String) {
        binding.tvNickname.text = getString(R.string.concern_type_nickname, nickName)
    }

    private fun setSelectedConcernTypeImage(
        binding: FragmentConcernTypeBinding,
        concerns: Concerns,
    ) = with(binding) {
        concerns.toMap().forEach { (type, isSelected) ->
            when (type) {
                ConcernType.Physical -> binding.imageViewConcernTypePhysical.setImageResource(
                    if (isSelected) R.drawable.cc_selected_physical_disability_icon
                    else R.drawable.cc_unselected_physical_disability_icon
                )

                ConcernType.Hear -> binding.imageViewConcernTypeHearing.setImageResource(
                    if (isSelected) R.drawable.cc_selected_hearing_impairment_icon
                    else R.drawable.cc_unselected_hearing_impairment_icon
                )

                ConcernType.Visual -> binding.imageViewConcernTypeVisual.setImageResource(
                    if (isSelected) R.drawable.cc_selected_visual_impairment_icon
                    else R.drawable.cc_unselected_visual_impairment_icon
                )

                ConcernType.Elderly -> binding.imageViewConcernTypeElderly.setImageResource(
                    if (isSelected) R.drawable.cc_selected_elderly_people_icon
                    else R.drawable.cc_unselected_elderly_people_icon
                )

                ConcernType.Child -> binding.imageViewConcernTypeInfant.setImageResource(
                    if (isSelected) R.drawable.cc_selected_infant_family_icon
                    else R.drawable.cc_unselected_infant_family_icon
                )
            }
        }
        if (requireContext().isTallBackEnabled()) {
            settingDescriptions(binding, concerns)
        }
    }

    private fun settingDescriptions(binding: FragmentConcernTypeBinding, concernType: Concerns) {
        val nicknameDescription = binding.tvNickname.text.toString()
        val selectedDescriptions = StringBuilder()
        concernType.selectedType().forEach { type ->
            when (type) {
                ConcernType.Physical -> selectedDescriptions.append(getString(R.string.text_physical_disability))
                ConcernType.Hear -> selectedDescriptions.append(getString(R.string.text_hearing_impairment))
                ConcernType.Visual -> selectedDescriptions.append(getString(R.string.text_visual_impairment))
                ConcernType.Elderly -> selectedDescriptions.append(getString(R.string.text_elderly_person))
                ConcernType.Child -> selectedDescriptions.append(getString(R.string.text_infant_family))
            }
        }
        val combinedDescription = "$nicknameDescription $selectedDescriptions"
        binding.concernTypeLayout.contentDescription = combinedDescription
    }

    private fun moveConcernTypeModify(binding: FragmentConcernTypeBinding) {
        binding.buttonConcernType.setOnClickListener {
            findNavController().navigate(R.id.action_concernTypeFragment_to_concernTypeModifyFragment)
        }
    }
}
