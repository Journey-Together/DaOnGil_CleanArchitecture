package kr.tekit.lion.presentation.scheduleform.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.domain.model.scheduleform.DailySchedule
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentModifyScheduleDetailsBinding
import kr.tekit.lion.presentation.home.DetailActivity
import kr.tekit.lion.presentation.scheduleform.FormDateFormat
import kr.tekit.lion.presentation.scheduleform.adapter.FormScheduleAdapter
import kr.tekit.lion.presentation.scheduleform.vm.ModifyScheduleFormViewModel

@AndroidEntryPoint
class ModifyScheduleDetailsFragment : Fragment(R.layout.fragment_modify_schedule_details) {

    private val viewModel: ModifyScheduleFormViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentModifyScheduleDetailsBinding.bind(view)

        initToolbar(binding)
        initView(binding)
    }

    private fun initToolbar(binding: FragmentModifyScheduleDetailsBinding) {
        binding.toolbarModifyDetails.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initView(binding: FragmentModifyScheduleDetailsBinding) {
        with(binding) {
            val title = viewModel.getScheduleTitle()
            textModifyDetailTitle.text = getString(R.string.text_selected_title, title)
            val period = viewModel.formatPickedDates(FormDateFormat.YYYY_MM_DD)
            textModifyDetailDate.text = getString(R.string.text_selected_period, period)

            buttonModiyDetailNextStep.setOnClickListener { view ->
                val action =
                    ModifyScheduleDetailsFragmentDirections.toModifyScheduleConfirmFragment()
                findNavController().navigate(action)
            }
        }

        viewModel.schedule.observe(viewLifecycleOwner) {
            it?.let {
                settingScheduleFormAdapter(binding, it)
            }
        }
    }

    private fun settingScheduleFormAdapter(
        binding: FragmentModifyScheduleDetailsBinding,
        dailyScheduleList: List<DailySchedule>
    ) {
        binding.recyclerViewModifyDetail.adapter = FormScheduleAdapter(
            dailyScheduleList,
            onAddButtonClickListener = { schedulePosition ->
                val action = ModifyScheduleDetailsFragmentDirections.toModifySearchFragment(
                    schedulePosition
                )
                findNavController().navigate(action)
            },
            onItemClickListener = { schedulePosition, placePosition ->
                val placeId = dailyScheduleList[schedulePosition].dailyPlaces[placePosition].placeId
                navigateToPlaceDetail(placeId)
            },
            onRemoveButtonClickListener = { schedulePosition, placePosition ->
                viewModel.removePlace(schedulePosition, placePosition)
            }
        )
    }

    private fun navigateToPlaceDetail(placeId: Long) {
        val intent = Intent(requireActivity(), DetailActivity::class.java)
        intent.putExtra("detailPlaceId", placeId)
        startActivity(intent)
    }
}