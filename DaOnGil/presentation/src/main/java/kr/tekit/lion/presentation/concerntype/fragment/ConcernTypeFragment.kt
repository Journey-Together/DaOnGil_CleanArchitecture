package kr.tekit.lion.presentation.concerntype.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kr.tekit.lion.domain.model.ConcernType
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.concerntype.vm.ConcernTypeViewModel
import kr.tekit.lion.presentation.databinding.FragmentConcernTypeBinding
import kr.tekit.lion.presentation.delegate.NetworkState
import kr.tekit.lion.presentation.ext.repeatOnViewStarted
import kr.tekit.lion.presentation.ext.showInfinitySnackBar
import kr.tekit.lion.presentation.observer.ConnectivityObserver
import kr.tekit.lion.presentation.observer.NetworkConnectivityObserver

@AndroidEntryPoint
class ConcernTypeFragment : Fragment(R.layout.fragment_concern_type) {

    private val viewModel: ConcernTypeViewModel by activityViewModels()
    private val connectivityObserver: ConnectivityObserver by lazy {
        NetworkConnectivityObserver.getInstance(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentConcernTypeBinding.bind(view)

        settingToolbar(binding)
        observeNickname(binding)
        observeSelection(binding)
        moveConcernTypeModify(binding)

        repeatOnViewStarted {
            supervisorScope {
                launch { collectConcernTypeState(binding) }
                launch { observeConnectivity(binding) }
            }
        }
    }

    private suspend fun collectConcernTypeState(binding: FragmentConcernTypeBinding) {
        with(binding) {
            viewModel.networkState.collect { networkState ->
                when (networkState) {
                    is NetworkState.Loading -> {
                        concernTypeProgressBar.visibility = View.VISIBLE
                    }
                    is NetworkState.Success -> {
                        concernTypeProgressBar.visibility = View.GONE
                    }
                    is NetworkState.Error -> {
                        concernTypeProgressBar.visibility = View.GONE
                        concernTypeLayout.visibility = View.GONE
                        concernTypeDivider.visibility = View.GONE
                        concernTypeModifyLayout.visibility = View.GONE
                        concernTypeErrorLayout.visibility = View.VISIBLE
                        concernTypeErrorMsg.text = networkState.msg
                    }
                }
            }
        }
    }

    private suspend fun observeConnectivity(binding: FragmentConcernTypeBinding) {
        with(binding) {
            connectivityObserver.getFlow().collect { connectivity ->
                Log.d("ConcernTypeFragment", "Connectivity status: $connectivity")
                when (connectivity) {
                    ConnectivityObserver.Status.Available -> {
                        concernTypeLayout.visibility = View.VISIBLE
                        concernTypeDivider.visibility = View.VISIBLE
                        concernTypeModifyLayout.visibility = View.VISIBLE
                        concernTypeErrorLayout.visibility = View.GONE
                    }
                    ConnectivityObserver.Status.Unavailable,
                    ConnectivityObserver.Status.Losing,
                    ConnectivityObserver.Status.Lost -> {
                        concernTypeLayout.visibility = View.GONE
                        concernTypeDivider.visibility = View.GONE
                        concernTypeModifyLayout.visibility = View.GONE
                        concernTypeErrorLayout.visibility = View.VISIBLE
                        val msg = "${getString(R.string.text_network_is_unavailable)}\n" +
                                "${getString(R.string.text_plz_check_network)} "
                        concernTypeErrorMsg.text = msg
                    }
                }
            }
        }
    }

    private fun settingToolbar(binding: FragmentConcernTypeBinding) {
        binding.toolbarConcernType.setNavigationOnClickListener {
            requireActivity().finish()
        }
    }

    private fun observeNickname(binding: FragmentConcernTypeBinding) {
        viewModel.nickName.observe(viewLifecycleOwner) { nickName ->
            binding.textViewConcernTypeUseNickname.text = getString(R.string.concern_type_nickname, nickName)
        }
    }

    private fun observeSelection(binding: FragmentConcernTypeBinding) {
        viewModel.concernType.observe(viewLifecycleOwner) { concernType ->
            initSelection(binding, concernType)
        }
    }

    private fun initSelection(binding: FragmentConcernTypeBinding, concernType: ConcernType) {
        with(binding) {
            if (concernType.isPhysical) {
                settingSelected(imageViewConcernTypePhysical, R.drawable.cc_selected_physical_disability_icon)
            }
            if (concernType.isVisual) {
                settingSelected(imageViewConcernTypeVisual, R.drawable.cc_selected_visual_impairment_icon)
            }
            if (concernType.isHear) {
                settingSelected(imageViewConcernTypeHearing, R.drawable.cc_selected_hearing_impairment_icon)
            }
            if (concernType.isChild) {
                settingSelected(imageViewConcernTypeInfant, R.drawable.cc_selected_infant_family_icon)
            }
            if (concernType.isElderly) {
                settingSelected(imageViewConcernTypeElderly, R.drawable.cc_selected_elderly_people_icon)
            }
        }
    }

    private fun settingSelected(imageView: ImageView, selectedDrawable: Int) {
        imageView.setImageResource(selectedDrawable)
    }

    private fun moveConcernTypeModify(binding: FragmentConcernTypeBinding) {
        binding.buttonConcernType.setOnClickListener {
            findNavController().navigate(R.id.action_concernTypeFragment_to_concernTypeModifyFragment)
        }
    }
}