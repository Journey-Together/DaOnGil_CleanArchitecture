package kr.techit.lion.presentation.concerntype.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kr.techit.lion.domain.model.ConcernType
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.concerntype.vm.ConcernTypeViewModel
import kr.techit.lion.presentation.databinding.FragmentConcernTypeModifyBinding
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.ext.isTallBackEnabled
import kr.techit.lion.presentation.ext.repeatOnViewStarted
import kr.techit.lion.presentation.ext.showInfinitySnackBar
import kr.techit.lion.presentation.ext.showSnackbar
import kr.techit.lion.presentation.observer.ConnectivityObserver
import kr.techit.lion.presentation.observer.NetworkConnectivityObserver

class ConcernTypeModifyFragment : Fragment(R.layout.fragment_concern_type_modify) {

    private val viewModel: ConcernTypeViewModel by activityViewModels()
    private val connectivityObserver: ConnectivityObserver by lazy {
        NetworkConnectivityObserver(requireContext().applicationContext)
    }
    private val selectedConcernType = mutableSetOf<Int>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentConcernTypeModifyBinding.bind(view)

        initView(binding)
        observeSelection(binding)
        concernTypeModify(binding)

        repeatOnViewStarted {
            supervisorScope {
                launch { collectConcernTypeModifyState() }
                launch { observeConnectivity(binding) }
            }
        }
    }

    private suspend fun collectConcernTypeModifyState() {
        viewModel.networkState.collect { networkState ->
            when (networkState) {
                is NetworkState.Loading -> {}
                is NetworkState.Success -> {}
                is NetworkState.Error -> {
                    requireContext().showSnackbar(requireView(), networkState.msg)
                }
            }
        }
    }

    private suspend fun observeConnectivity(binding: FragmentConcernTypeModifyBinding) {
        with(binding) {
            connectivityObserver.getFlow().collect { connectivity ->
                when (connectivity) {
                    ConnectivityObserver.Status.Available -> {
                        buttonConcernTypeModify.isEnabled = true
                    }
                    ConnectivityObserver.Status.Unavailable,
                    ConnectivityObserver.Status.Losing,
                    ConnectivityObserver.Status.Lost -> {
                        buttonConcernTypeModify.isEnabled = false
                        val msg = "${getString(R.string.text_network_is_unavailable)}\n" +
                                "${getString(R.string.text_plz_check_network)} "
                        requireContext().showInfinitySnackBar(buttonConcernTypeModify, msg)
                    }
                }
            }
        }
    }
    
    private fun initView(binding: FragmentConcernTypeModifyBinding) {
        with(binding) {
            toolbarConcernTypeModify.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            toolbarConcernTypeModify.setNavigationContentDescription(R.string.text_back_button)

            imageViewConcernTypeModifyPhysical.setOnClickListener {
                toggleSelection(it as ImageView, R.drawable.physical_no_select, R.drawable.physical_select, binding)
            }
            imageViewConcernTypeModifyVisual.setOnClickListener {
                toggleSelection(it as ImageView, R.drawable.visual_no_select, R.drawable.visual_select, binding)
            }
            imageViewConcernTypeModifyHearing.setOnClickListener {
                toggleSelection(it as ImageView, R.drawable.hearing_no_select, R.drawable.hearing_select, binding)
            }
            imageViewConcernTypeModifyInfant.setOnClickListener {
                toggleSelection(it as ImageView, R.drawable.infant_family_no_select, R.drawable.infant_family_select, binding)
            }
            imageViewConcernTypeModifyElderly.setOnClickListener {
                toggleSelection(it as ImageView, R.drawable.elderly_people_no_select, R.drawable.elderly_people_select, binding)
            }
        }
    }

    private fun observeSelection(binding: FragmentConcernTypeModifyBinding) {
        viewModel.concernType.observe(viewLifecycleOwner) { concernType ->
            initSelection(binding, concernType)
            if (requireContext().isTallBackEnabled()) {
                settingDescriptions(binding, concernType)
            }
        }
    }

    private fun initSelection(binding: FragmentConcernTypeModifyBinding, concernType: ConcernType) {
        with(binding) {
            if (concernType.isPhysical) {
                settingSelected(imageViewConcernTypeModifyPhysical, R.drawable.physical_select)
            }
            if (concernType.isVisual) {
                settingSelected(imageViewConcernTypeModifyVisual, R.drawable.visual_select)
            }
            if (concernType.isHear) {
                settingSelected(imageViewConcernTypeModifyHearing, R.drawable.hearing_select)
            }
            if (concernType.isChild) {
                settingSelected(imageViewConcernTypeModifyInfant, R.drawable.infant_family_select)
            }
            if (concernType.isElderly) {
                settingSelected(imageViewConcernTypeModifyElderly, R.drawable.elderly_people_select)
            }
        }

        updateModifyButtonState(binding)
    }

    private fun settingDescriptions(binding: FragmentConcernTypeModifyBinding, concernType: ConcernType) {
        val titleDescription = binding.textViewConcernTypeModifyTitle.text.toString()
        val selectedDescriptions = mutableListOf<String>()

        if (concernType.isPhysical) {
            selectedDescriptions.add(getString(R.string.text_physical_disability))
        }
        if (concernType.isVisual) {
            selectedDescriptions.add(getString(R.string.text_visual_impairment))
        }
        if (concernType.isHear) {
            selectedDescriptions.add(getString(R.string.text_hearing_impairment))
        }
        if (concernType.isChild) {
            selectedDescriptions.add(getString(R.string.text_infant_family))
        }
        if (concernType.isElderly) {
            selectedDescriptions.add(getString(R.string.text_elderly_person))
        }

        val contentDescription = if (selectedDescriptions.isNotEmpty()) {
            selectedDescriptions.joinToString(separator = ", ")
        } else {
            getString(R.string.text_no_concern_type_selected)
        }

        val combinedDescription = "$titleDescription, 현재 선택된 관심 유형은 $contentDescription 입니다"

        binding.textViewConcernTypeModifyTitle.contentDescription = combinedDescription
    }

    private fun settingSelected(imageView: ImageView, selectedDrawable: Int) {
        imageView.setImageResource(selectedDrawable)
        imageView.tag = "true"
        selectedConcernType.add(imageView.id)
    }

    private fun toggleSelection(imageView: ImageView, unselectedDrawable: Int, selectedDrawable: Int, binding: FragmentConcernTypeModifyBinding) {
        if (imageView.tag == "true") {
            imageView.setImageResource(unselectedDrawable)
            imageView.tag = "false"
            selectedConcernType.remove(imageView.id)
        } else {
            imageView.setImageResource(selectedDrawable)
            imageView.tag = "true"
            selectedConcernType.add(imageView.id)
        }

        updateModifyButtonState(binding)
    }

    private fun updateModifyButtonState(binding: FragmentConcernTypeModifyBinding) {
        binding.buttonConcernTypeModify.isEnabled = selectedConcernType.isNotEmpty()
    }

    private fun concernTypeModify(binding: FragmentConcernTypeModifyBinding) {
        binding.buttonConcernTypeModify.setOnClickListener {
            val isPhysical = binding.imageViewConcernTypeModifyPhysical.tag.toString().toBoolean()
            val isHear = binding.imageViewConcernTypeModifyHearing.tag.toString().toBoolean()
            val isVisual = binding.imageViewConcernTypeModifyVisual.tag.toString().toBoolean()
            val isElderly = binding.imageViewConcernTypeModifyElderly.tag.toString().toBoolean()
            val isChild = binding.imageViewConcernTypeModifyInfant.tag.toString().toBoolean()

            viewModel.updateConcernType(ConcernType(isPhysical, isHear, isVisual, isElderly, isChild))

            viewModel.snackbarEvent.observe(viewLifecycleOwner) { message ->
                message?.let {
                    requireContext().showSnackbar(requireView(), message)
                    viewModel.resetSnackbarEvent()
                    findNavController().popBackStack()
                }
            }
        }
    }
}