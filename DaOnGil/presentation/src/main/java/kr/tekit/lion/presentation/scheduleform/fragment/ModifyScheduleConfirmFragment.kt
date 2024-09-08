package kr.tekit.lion.presentation.scheduleform.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentModifyScheduleConfirmBinding

class ModifyScheduleConfirmFragment : Fragment(R.layout.fragment_modify_schedule_confirm) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentModifyScheduleConfirmBinding.bind(view)
    }
}