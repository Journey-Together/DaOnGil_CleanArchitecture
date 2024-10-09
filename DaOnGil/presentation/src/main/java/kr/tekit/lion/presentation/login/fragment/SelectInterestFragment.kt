package kr.tekit.lion.presentation.login.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kr.tekit.lion.domain.model.ConcernType
import kr.tekit.lion.domain.model.hasAnyTrue
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentSelectInterestBinding
import kr.tekit.lion.presentation.delegate.NetworkState
import kr.tekit.lion.presentation.ext.isTallBackEnabled
import kr.tekit.lion.presentation.ext.repeatOnViewStarted
import kr.tekit.lion.presentation.login.model.InterestType
import kr.tekit.lion.presentation.login.vm.InterestViewModel
import kr.tekit.lion.presentation.main.MainActivity

@AndroidEntryPoint
class SelectInterestFragment : Fragment(R.layout.fragment_select_interest) {

    private val viewModel: InterestViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSelectInterestBinding.bind(view)

        val interestImageViews = mapOf(
            InterestType.Physical to binding.physicalDisabilityImageView,
            InterestType.Hear to binding.hearingImpairmentImageView,
            InterestType.Visual to binding.visualImpairmentImageView,
            InterestType.Elderly to binding.elderlyPeopleImageView,
            InterestType.Child to binding.infantFamilyImageView
        )

        interestImageViews.map { (type, imageView) ->
            imageView.setOnClickListener {
                viewModel.onSelectInterest(type)
            }
        }

        binding.selectInterestCompleteButton.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.onClickSubmitButton()
        }

        repeatOnViewStarted {
            supervisorScope {
                launch { collectNetworkState(binding) }
                launch { collectConcernType(binding) }
            }
        }
    }

    private suspend fun collectNetworkState(binding: FragmentSelectInterestBinding){
        viewModel.networkState.collect {
            when(it) {
                is NetworkState.Loading -> return@collect
                is NetworkState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, it.msg, Snackbar.LENGTH_SHORT).show()
                }
                is NetworkState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()
                }
            }
        }
    }

    private suspend fun collectConcernType(binding: FragmentSelectInterestBinding) {
        viewModel.concernType.collectLatest { concernType ->
            updateUI(binding, concernType)
        }
    }

    private fun updateUI(binding: FragmentSelectInterestBinding, concernType: ConcernType) {
        binding.physicalDisabilityImageView.setImageResource(
            if (concernType.isPhysical) R.drawable.physical_select else R.drawable.physical_no_select
        )
        binding.hearingImpairmentImageView.setImageResource(
            if (concernType.isHear) R.drawable.hearing_select else R.drawable.hearing_no_select
        )
        binding.visualImpairmentImageView.setImageResource(
            if (concernType.isVisual) R.drawable.visual_select else R.drawable.visual_no_select
        )
        binding.elderlyPeopleImageView.setImageResource(
            if (concernType.isElderly) R.drawable.elderly_people_select else R.drawable.elderly_people_no_select
        )
        binding.infantFamilyImageView.setImageResource(
            if (concernType.isChild) R.drawable.infant_family_select else R.drawable.infant_family_no_select
        )

        val anySelected = concernType.hasAnyTrue()
        binding.selectInterestCompleteButton.isEnabled = anySelected
        if (requireContext().isTallBackEnabled() && !anySelected){
            binding.selectInterestCompleteButton.contentDescription = "관심유형을 한개 이상선택해주세요"
        }
    }
}