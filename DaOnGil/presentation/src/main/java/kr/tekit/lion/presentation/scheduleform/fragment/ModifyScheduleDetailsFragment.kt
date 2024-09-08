package kr.tekit.lion.presentation.scheduleform.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentModifyScheduleDetailsBinding

@AndroidEntryPoint
class ModifyScheduleDetailsFragment : Fragment(R.layout.fragment_modify_schedule_details) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentModifyScheduleDetailsBinding.bind(view)
    }
}