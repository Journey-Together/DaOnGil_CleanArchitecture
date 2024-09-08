package kr.tekit.lion.presentation.login.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kr.tekit.lion.domain.model.ConcernType
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
            viewModel.onClickSubmitButton()
        }

        repeatOnViewStarted {
            supervisorScope {
                launch {
                    viewModel.errorMessage.collect {
                        if (it != null) Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                    }
                }

                launch {
                    viewModel.networkState.collect {
                        if (it == NetworkState.Success) {
                            Log.d("czxcdsd", "NetworkState Success")

                            viewModel.saveUserActivation{
                                Log.d("czxcdsd", "startActivity")

                                startActivity(Intent(requireActivity(), MainActivity::class.java))
                                requireActivity().finish()
                            }
                        }
                    }
                }

                launch {
                    viewModel.concernType.collectLatest { concernType ->
                        updateUI(binding, concernType)
                    }
                }
            }
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

        val anySelected = concernType.isPhysical || concernType.isHear || concernType.isVisual ||
                concernType.isElderly || concernType.isChild
        binding.selectInterestCompleteButton.isEnabled = anySelected
        if (requireContext().isTallBackEnabled() && !anySelected){
            binding.selectInterestCompleteButton.contentDescription = "관심유형을 한개 이상선택해주세요"
        }
    }
}