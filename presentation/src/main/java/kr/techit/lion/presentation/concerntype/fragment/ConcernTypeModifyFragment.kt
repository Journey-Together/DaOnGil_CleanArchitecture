package kr.techit.lion.presentation.concerntype.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import kr.techit.lion.domain.model.concern.ConcernType
import kr.techit.lion.domain.model.concern.Concerns
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.concerntype.ConcernTypeUiEvent
import kr.techit.lion.presentation.concerntype.vm.ConcernTypeViewModel
import kr.techit.lion.presentation.connectivity.ConnectivityObserver
import kr.techit.lion.presentation.databinding.FragmentConcernTypeModifyBinding
import kr.techit.lion.presentation.delegate.NetworkEvent
import kr.techit.lion.presentation.ext.repeatOnViewStarted
import kr.techit.lion.presentation.ext.showInfinitySnackBar
import kr.techit.lion.presentation.ext.showSnackbar

class ConcernTypeModifyFragment : Fragment(R.layout.fragment_concern_type_modify) {
    private val viewModel: ConcernTypeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentConcernTypeModifyBinding.bind(view)

        initView(binding)

        repeatOnViewStarted {
            launch { collectNetworkEvent(binding) }
            launch { collectConnectivity(binding) }
            launch { collectUiState(binding) }
            launch { collectUiEvent() }
        }
    }

    private fun initView(binding: FragmentConcernTypeModifyBinding) {
        with(binding) {
            toolbarConcernTypeModify.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            toolbarConcernTypeModify.setNavigationContentDescription(R.string.text_back_button)

            imageViewConcernTypeModifyPhysical.setOnClickListener {
                viewModel.updateConcernType(ConcernType.Physical)
            }
            imageViewConcernTypeModifyVisual.setOnClickListener {
                viewModel.updateConcernType(ConcernType.Visual)
            }
            imageViewConcernTypeModifyHearing.setOnClickListener {
                viewModel.updateConcernType(ConcernType.Hear)
            }
            imageViewConcernTypeModifyInfant.setOnClickListener {
                viewModel.updateConcernType(ConcernType.Child)
            }
            imageViewConcernTypeModifyElderly.setOnClickListener {
                viewModel.updateConcernType(ConcernType.Elderly)
            }
            buttonConcernTypeModify.setOnClickListener {
                viewModel.saveModifiedConcernType()
            }
        }
    }

    private suspend fun collectUiState(binding: FragmentConcernTypeModifyBinding) {
        viewModel.uiState.collect { uiState ->
            setConcernTypeImage(binding, uiState.concernType)
        }
    }

    private fun setConcernTypeImage(
        binding: FragmentConcernTypeModifyBinding,
        concernType: Concerns,
    ) {
        with(binding) {
            concernType.toMap().forEach { type, isSelected ->
                when (type) {
                    ConcernType.Physical -> binding.imageViewConcernTypeModifyPhysical.setImageResource(
                        if (isSelected) R.drawable.cc_selected_physical_disability_icon else R.drawable.physical_no_select
                    )

                    ConcernType.Hear -> binding.imageViewConcernTypeModifyHearing.setImageResource(
                        if (isSelected) R.drawable.cc_selected_hearing_impairment_icon else R.drawable.hearing_no_select
                    )

                    ConcernType.Visual -> binding.imageViewConcernTypeModifyVisual.setImageResource(
                        if (isSelected) R.drawable.cc_selected_visual_impairment_icon else R.drawable.visual_no_select
                    )

                    ConcernType.Elderly -> binding.imageViewConcernTypeModifyElderly.setImageResource(
                        if (isSelected) R.drawable.cc_selected_elderly_people_icon else R.drawable.elderly_people_no_select
                    )

                    ConcernType.Child -> binding.imageViewConcernTypeModifyInfant.setImageResource(
                        if (isSelected) R.drawable.cc_selected_infant_family_icon else R.drawable.infant_family_no_select
                    )
                }
            }
        }
        settingDescriptions(binding, concernType)
    }


    private fun settingDescriptions(
        binding: FragmentConcernTypeModifyBinding,
        concernType: Concerns,
    ) {
        val titleDescription = binding.textViewConcernTypeModifyTitle.text.toString()
        val selectedDescriptions = StringBuilder().apply {
            concernType.selectedType().forEach { type ->
                when (type) {
                    ConcernType.Physical -> append(getString(R.string.text_physical_disability))
                    ConcernType.Hear -> append(getString(R.string.text_hearing_impairment))
                    ConcernType.Visual -> append(getString(R.string.text_visual_impairment))
                    ConcernType.Elderly -> append(getString(R.string.text_elderly_person))
                    ConcernType.Child -> append(getString(R.string.text_infant_family))
                }
            }
        }

        val combinedDescription =
            requireContext().getString(R.string.description_modify_concern_type)
                .format(titleDescription, selectedDescriptions)
        binding.textViewConcernTypeModifyTitle.contentDescription = combinedDescription
    }

    private suspend fun collectUiEvent() {
        viewModel.uiEvent.collect { event ->
            when (event) {
                ConcernTypeUiEvent.NavigateToBack -> findNavController().popBackStack()
            }
        }
    }


    private suspend fun collectNetworkEvent(binding: FragmentConcernTypeModifyBinding) {
        viewModel.networkEvent.collect { networkState ->
            when (networkState) {
                NetworkEvent.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is NetworkEvent.Success -> {
                    binding.progressBar.visibility = View.GONE
                }

                is NetworkEvent.Error -> {
                    binding.progressBar.visibility = View.GONE
                    requireContext().showSnackbar(requireView(), networkState.msg)
                }
            }
        }
    }

    private suspend fun collectConnectivity(binding: FragmentConcernTypeModifyBinding) {
        with(binding) {
            viewModel.connectivityState.collect { connectivity ->
                when (connectivity) {
                    ConnectivityObserver.Status.Available ->
                        buttonConcernTypeModify.isEnabled = true

                    else -> {
                        buttonConcernTypeModify.isEnabled = false
                        requireContext().showInfinitySnackBar(
                            buttonConcernTypeModify,
                            requireContext().getString(R.string.can_not_access_network)
                        )
                    }
                }
            }
        }
    }
}
