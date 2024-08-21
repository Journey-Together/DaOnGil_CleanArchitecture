package kr.tekit.lion.presentation.myinfo.fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentMyInfoBinding
import kr.tekit.lion.presentation.delegate.NetworkState
import kr.tekit.lion.presentation.ext.repeatOnViewStarted
import kr.tekit.lion.presentation.myinfo.vm.MyInfoViewModel

@AndroidEntryPoint
class MyInfoFragment : Fragment(R.layout.fragment_my_info) {

    private val viewModel: MyInfoViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMyInfoBinding.bind(view)
        startShimmer(binding)
        with(binding) {
            toolbarMyInfo.setNavigationOnClickListener {
                handleBackPress()
            }

            repeatOnViewStarted {
                supervisorScope {
                    launch {
                        viewModel.name.collect {
                            tvName.text = it
                        }
                    }

                    launch {
                        viewModel.myPersonalInfo.collect{
                            tvNickname.text = it.nickname
                            tvPhone.text = it.phone
                        }
                    }

                    launch {
                        viewModel.myIceInfo.collect{
                            tvBirth.text = it.birth
                            tvBloodType.text = it.bloodType
                            tvDisease.text = it.disease
                            tvAllergy.text = it.allergy
                            tvMedicine.text = it.medication
                            tvRelation1.text = it.part1Rel
                            tvContact1.text = it.part1Phone
                            tvRelation2.text = it.part2Rel
                            tvContact2.text = it.part2Phone
                        }
                    }
                    launch {
                        viewModel.networkState.collect {
                            if (it == NetworkState.Success) stopShimmer(binding)
                        }
                    }
                    launch {
                        viewModel.networkState.collect {
                            if (it == NetworkState.Success) {
                                stopShimmer(binding)
                            }
                        }
                    }
                }
            }

            btnPersonalInfoModify.setOnClickListener {
                findNavController().navigate(R.id.action_myInfoFragment_to_personalInfoModifyFragment)
            }

            bntIceModify.setOnClickListener {
                findNavController().navigate(R.id.action_myInfoFragment_to_iceModifyFragment)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackPress()
        }
    }

    private fun startShimmer(binding: FragmentMyInfoBinding) {
        with(binding) {
            shimmerFrameLayout.startShimmer()
            shimmerFrameLayout.visibility = View.VISIBLE
        }
    }

    private fun stopShimmer(binding: FragmentMyInfoBinding) {
        with(binding){
            shimmerFrameLayout.stopShimmer()
            shimmerFrameLayout.visibility = View.GONE
            mainContainer.visibility = View.VISIBLE
        }
    }

    private fun handleBackPress() {
        if (viewModel.isPersonalInfoModified.value) {
            requireActivity().setResult(Activity.RESULT_OK)
        }
        requireActivity().finish()
    }
}