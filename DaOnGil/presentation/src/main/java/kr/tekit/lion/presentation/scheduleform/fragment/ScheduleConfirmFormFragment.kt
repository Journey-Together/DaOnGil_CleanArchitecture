package kr.tekit.lion.presentation.scheduleform.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kr.tekit.lion.domain.model.scheduleform.DailySchedule
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentScheduleConfirmFormBinding
import kr.tekit.lion.presentation.ext.showSnackbar
import kr.tekit.lion.presentation.scheduleform.adapter.FormConfirmScheduleAdapter
import kr.tekit.lion.presentation.scheduleform.vm.ScheduleFormViewModel


class ScheduleConfirmFormFragment : Fragment(R.layout.fragment_schedule_confirm_form) {
    private val viewModel: ScheduleFormViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentScheduleConfirmFormBinding.bind(view)

        initToolbar(binding)
        initConfirmView(binding)
        initButtonSubmitSchedule(binding)
    }

    private fun initToolbar(binding: FragmentScheduleConfirmFormBinding) {
        binding.toolbarScheduleConfirmForm.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initConfirmView(binding: FragmentScheduleConfirmFormBinding){
        val title = viewModel.getScheduleTitle()
        val period = viewModel.getSchedulePeriod()
        binding.apply {
            textScfTitle.text = getString(R.string.text_selected_title, title)
            textScfDate.text = getString(R.string.text_selected_period, period)
        }

        viewModel.schedule.observe(viewLifecycleOwner) {
            if(it != null){
                settingConfirmScheduleAdapter(binding, it)
            }
        }
    }

    private fun settingConfirmScheduleAdapter(
        binding: FragmentScheduleConfirmFormBinding,
        dailyScheduleList: List<DailySchedule>
    ) {
        binding.recyclerViewScf.adapter = FormConfirmScheduleAdapter(dailyScheduleList)
    }

    private fun initButtonSubmitSchedule(binding: FragmentScheduleConfirmFormBinding){
        binding.buttonScheduleFormSubmit.setOnClickListener { view ->
            viewModel.submitNewPlan{ _, requestFlag ->
                if(requestFlag){
                    // TODO 주석 해제
//                    requireActivity().setResult(ResultCode.RESULT_SCHEDULE_EDIT)
                    requireActivity().finish()
                }else{
                    view.showSnackbar("다시 시도해주세요")
                }
            }
        }
    }
}