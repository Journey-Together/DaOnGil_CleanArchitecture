package kr.techit.lion.presentation.login.concern

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.techit.lion.domain.model.concern.ConcernType
import kr.techit.lion.domain.model.concern.Concerns
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.FragmentSelectConcernBinding
import kr.techit.lion.presentation.delegate.NetworkEvent
import kr.techit.lion.presentation.ext.isTallBackEnabled
import kr.techit.lion.presentation.ext.repeatOnViewStarted
import kr.techit.lion.presentation.main.MainActivity

@AndroidEntryPoint
class SelectConcernFragment : Fragment(R.layout.fragment_select_concern) {

    private val viewModel: ConcernViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSelectConcernBinding.bind(view)

        initView(binding)

        repeatOnViewStarted {
            launch { collectUiState(binding) }
            launch { collectUiEvent() }
            launch { collectNetworkEvent(binding) }
        }
    }

    private fun initView(binding: FragmentSelectConcernBinding) = with(binding) {
        val interestImageViews = mapOf(
            ConcernType.Physical to physicalDisabilityImageView,
            ConcernType.Hear to hearingImpairmentImageView,
            ConcernType.Visual to visualImpairmentImageView,
            ConcernType.Elderly to elderlyPeopleImageView,
            ConcernType.Child to infantFamilyImageView
        )

        interestImageViews.map { (type, imageView) ->
            imageView.setOnClickListener {
                viewModel.modifyInterest(type)
            }
        }

        btnSubmit.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            viewModel.fetchConcern()
        }
    }

    private suspend fun collectUiState(binding: FragmentSelectConcernBinding) {
        viewModel.uiState.collect { concerns ->
            updateUI(binding, concerns)
        }
    }

    private suspend fun collectUiEvent() {
        viewModel.uiEvent.collect { event ->
            when (event) {
                ConcernUiEvent.NavigateToMain -> moveToMain()
            }
        }
    }

    private suspend fun collectNetworkEvent(binding: FragmentSelectConcernBinding) = with(binding) {
        viewModel.networkEvent.collect { event ->
            when (event) {
                NetworkEvent.Loading -> Unit
                NetworkEvent.Success -> {
                    progressBar.visibility = View.GONE
                    moveToMain()
                }

                is NetworkEvent.Error -> {
                    progressBar.visibility = View.GONE
                    showErrorSnackBar(binding)
                }
            }
        }
    }

    private fun moveToMain() {
        startActivity(MainActivity.newIntent(requireContext()))
        requireActivity().finish()
    }

    private fun showErrorSnackBar(binding: FragmentSelectConcernBinding) {
        Snackbar.make(
            binding.root,
            getString(R.string.plz_retry),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun updateUI(binding: FragmentSelectConcernBinding, concerns: Concerns) {
        concerns.toMap().forEach { (type, isSelected) ->
            when (type) {
                ConcernType.Physical -> binding.physicalDisabilityImageView.setImageResource(
                    if (isSelected) R.drawable.physical_select else R.drawable.physical_no_select
                )

                ConcernType.Hear -> binding.hearingImpairmentImageView.setImageResource(
                    if (isSelected) R.drawable.hearing_select else R.drawable.hearing_no_select
                )

                ConcernType.Visual -> binding.visualImpairmentImageView.setImageResource(
                    if (isSelected) R.drawable.visual_select else R.drawable.visual_no_select
                )

                ConcernType.Elderly -> binding.elderlyPeopleImageView.setImageResource(
                    if (isSelected) R.drawable.elderly_people_select else R.drawable.elderly_people_no_select
                )

                ConcernType.Child -> binding.infantFamilyImageView.setImageResource(
                    if (isSelected) R.drawable.infant_family_select else R.drawable.infant_family_no_select
                )
            }
        }

        val anySelected = concerns.anyTrue()
        binding.btnSubmit.isEnabled = concerns.anyTrue()
        if (requireContext().isTallBackEnabled() && !anySelected) {
            binding.btnSubmit.contentDescription = getString(R.string.plz_select_interest_type)
        }
    }
}
