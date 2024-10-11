package kr.techit.lion.presentation.scheduleform.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kr.techit.lion.domain.model.scheduleform.DailySchedule
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.FragmentScheduleDetailsFormBinding
import kr.techit.lion.presentation.ext.setAccessibilityText
import kr.techit.lion.presentation.home.DetailActivity
import kr.techit.lion.presentation.scheduleform.FormDateFormat.YYYY_MM_DD
import kr.techit.lion.presentation.scheduleform.adapter.FormScheduleAdapter
import kr.techit.lion.presentation.scheduleform.vm.ScheduleFormViewModel


@AndroidEntryPoint
class ScheduleDetailsFormFragment : Fragment(R.layout.fragment_schedule_details_form) {

    private val viewModel: ScheduleFormViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentScheduleDetailsFormBinding.bind(view)

        initToolbar(binding)

        viewModel.initScheduleList()
        initView(binding)
    }

    private fun initToolbar(binding: FragmentScheduleDetailsFormBinding) {
        binding.toolbarScheduleDetailForm.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initView(binding: FragmentScheduleDetailsFormBinding) {
        with(binding) {
            val title = viewModel.getScheduleTitle()
            textSdfTitle.text = getString(R.string.text_selected_title, title)
            val period = viewModel.getSchedulePeriod(YYYY_MM_DD)
            textSdfDate.apply {
                text = getString(R.string.text_selected_period, period)
                setAccessibilityText(viewModel.getSchedulePeriodAccessibilityText())
            }

            buttonSdfNextStep.setOnClickListener {
                navigateToConfirmScreen()
            }
        }

        viewModel.schedule.observe(viewLifecycleOwner) {
            if (it != null) {
                settingScheduleFormAdapter(binding, it)
            }
        }
    }

    private fun settingScheduleFormAdapter(
        binding: FragmentScheduleDetailsFormBinding,
        dailyScheduleList: List<DailySchedule>,
    ) {
        binding.recyclerViewSdf.adapter = FormScheduleAdapter(
            dailyScheduleList,
            onAddButtonClickListener = { schedulePosition ->
                // 몇 번째 일정에 여행지를 추가하는지 파악하기 위해 schedulePosition 을 전달해준다.
                val action =
                    ScheduleDetailsFormFragmentDirections.toFormSearchFragment(
                        schedulePosition
                    )
                findNavController().navigate(action)
            },
            onItemClickListener = { schedulePosition, placePosition ->
                val placeId = dailyScheduleList[schedulePosition].dailyPlaces[placePosition].placeId
                navigateToPlaceDetail(placeId)
            },
            onRemoveButtonClickListener = { schedulePosition, placePosition ->
                // viewModel에서 해당 place 제거
                viewModel.removePlace(schedulePosition, placePosition)
            }
        )
    }

    private fun navigateToConfirmScreen() {
        val navController = findNavController()
        val action = ScheduleDetailsFormFragmentDirections.toFormScheduleConfirmFragment()
        navController.navigate(action)
    }

    private fun navigateToPlaceDetail(placeId: Long) {
        val intent = Intent(requireActivity(), DetailActivity::class.java)
        intent.putExtra("detailPlaceId", placeId)
        startActivity(intent)
    }

}