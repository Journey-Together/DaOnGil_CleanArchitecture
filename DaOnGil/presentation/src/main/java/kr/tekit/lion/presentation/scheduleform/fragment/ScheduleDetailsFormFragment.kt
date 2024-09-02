package kr.tekit.lion.presentation.scheduleform.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentScheduleDetailsFormBinding
import kr.tekit.lion.presentation.scheduleform.vm.ScheduleFormViewModel


@AndroidEntryPoint
class ScheduleDetailsFormFragment : Fragment(R.layout.fragment_schedule_details_form) {

    private val viewModel: ScheduleFormViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentScheduleDetailsFormBinding.bind(view)

        initToolbar(binding)

        viewModel.initScheduleList()
//        initView(binding)
    }

    private fun initToolbar(binding: FragmentScheduleDetailsFormBinding) {
        binding.toolbarScheduleDetailForm.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }


}