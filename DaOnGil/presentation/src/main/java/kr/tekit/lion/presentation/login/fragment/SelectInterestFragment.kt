package kr.tekit.lion.presentation.login.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.model.ConcernType
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentSelectInterestBinding
import kr.tekit.lion.presentation.ext.repeatOnViewStarted
import kr.tekit.lion.presentation.login.vm.InterestViewModel
import kr.tekit.lion.presentation.main.MainActivity

@AndroidEntryPoint
class SelectInterestFragment : Fragment(R.layout.fragment_select_interest) {

    private val viewModel: InterestViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSelectInterestBinding.bind(view)

        val interestImageViews = mapOf(
            1 to binding.physicalDisabilityImageView,
            2 to binding.hearingImpairmentImageView,
            3 to binding.visualImpairmentImageView,
            4 to binding.elderlyPeopleImageView,
            5 to binding.infantFamilyImageView
        )

        interestImageViews.map { (typeNo, imageView) ->
            imageView.setOnClickListener {
                viewModel.onSelectInterest(typeNo)
            }
        }

        binding.selectInterestCompleteButton.setOnClickListener {

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.onClickSubmitButton()
            }

            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()

        }

        repeatOnViewStarted {
            viewModel.concernType.collectLatest { concernType ->
                updateUI(binding, concernType)
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
    }
}