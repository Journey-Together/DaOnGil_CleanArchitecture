package kr.techit.lion.presentation.scheduleform.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.techit.lion.domain.model.scheduleform.DailySchedule
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.FragmentModifyScheduleConfirmBinding
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.ext.setAccessibilityText
import kr.techit.lion.presentation.ext.showSnackbar
import kr.techit.lion.presentation.schedule.ResultCode
import kr.techit.lion.presentation.scheduleform.FormDateFormat
import kr.techit.lion.presentation.scheduleform.adapter.FormConfirmScheduleAdapter
import kr.techit.lion.presentation.scheduleform.vm.ModifyScheduleFormViewModel

@AndroidEntryPoint
class ModifyScheduleConfirmFragment : Fragment(R.layout.fragment_modify_schedule_confirm) {

    private val viewModel: ModifyScheduleFormViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentModifyScheduleConfirmBinding.bind(view)

        settingProgressBarVisibility(binding)

        initToolbar(binding)
        initView(binding)
    }

    private fun settingProgressBarVisibility(binding: FragmentModifyScheduleConfirmBinding) {
        // NetworkState의 기본값이 Loading 이기 때문에, ProgressBar가 보이지 않도록 Success로 바꿔준다
        viewModel.resetNetworkState()

        with(binding) {
            lifecycleScope.launch {
                viewModel.networkState.collect { state ->
                    when(state){
                        is NetworkState.Loading -> {
                            progressBarModifyConfirm.visibility = View.VISIBLE
                        }
                        is NetworkState.Success -> {
                            progressBarModifyConfirm.visibility = View.GONE
                        }
                        is NetworkState.Error -> {
                            progressBarModifyConfirm.visibility = View.GONE
                            val errorMsg = state.msg.replace("\n ".toRegex(), "\n")
                            buttonModifyFormSubmit.showSnackbar(errorMsg)
                        }

                    }
                }
            }
        }
    }

    private fun initToolbar(binding: FragmentModifyScheduleConfirmBinding) {
        binding.toolbarModifyConfirmForm.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initView(binding: FragmentModifyScheduleConfirmBinding) {
        val title = viewModel.getScheduleTitle()
        val period = viewModel.formatPickedDates(FormDateFormat.YYYY_MM_DD)
        binding.apply {
            textMcfTitle.text = getString(R.string.text_selected_title, title)
            textMcfDate.apply {
                text = getString(R.string.text_selected_period, period)
                setAccessibilityText(viewModel.getSchedulePeriodAccessibilityText())
            }
            buttonModifyFormSubmit.setOnClickListener {
                submitPlan(it)
            }
        }

        viewModel.schedule.observe(viewLifecycleOwner) {
            if (it != null) settingConfirmScheduleAdapter(binding, it)
        }
    }

    private fun settingConfirmScheduleAdapter(
        binding: FragmentModifyScheduleConfirmBinding,
        dailyScheduleList: List<DailySchedule>
    ) {
        binding.recyclerViewMcf.adapter = FormConfirmScheduleAdapter(dailyScheduleList)
    }

    private fun submitPlan(view: View) {
        viewModel.submitRevisedSchedule() { _, flag ->
            if (flag) {
                view.showSnackbar("여행 일정이 수정되었습니다")

                requireActivity().setResult(ResultCode.RESULT_SCHEDULE_EDIT)
                requireActivity().finish()
            }
        }
    }
}