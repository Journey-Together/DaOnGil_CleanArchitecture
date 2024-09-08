package kr.tekit.lion.presentation.scheduleform.fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentModifyNamePeriodBinding
import kr.tekit.lion.presentation.scheduleform.model.OriginalScheduleInfo
import kr.tekit.lion.presentation.scheduleform.vm.ModifyScheduleFormViewModel

@AndroidEntryPoint
class ModifyNamePeriodFragment : Fragment(R.layout.fragment_modify_name_period) {

    private val viewModel: ModifyScheduleFormViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentModifyNamePeriodBinding.bind(view)

        initScheduleData(binding)

    }

    private fun initScheduleData(binding:FragmentModifyNamePeriodBinding){
        val originalScheduleInfo = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().intent.getParcelableExtra("scheduleInfo", OriginalScheduleInfo::class.java)
        } else {
            requireActivity().intent.getParcelableExtra("scheduleInfo") as? OriginalScheduleInfo
        }

        originalScheduleInfo?.let {
            viewModel.initScheduleDetailInfo(it)
        }

        initScehduleTitleAndPeriod(binding)
    }

    private fun initScehduleTitleAndPeriod(binding:FragmentModifyNamePeriodBinding){
        viewModel.title.observe(viewLifecycleOwner){
            binding.editModiyNpfTitle.setText(it)
        }
        viewModel.endDate.observe(viewLifecycleOwner){
            val pickedDates = viewModel.formatPickedDates()
            binding.buttonModifyNpfSetPeriod.text = pickedDates
        }

    }
}