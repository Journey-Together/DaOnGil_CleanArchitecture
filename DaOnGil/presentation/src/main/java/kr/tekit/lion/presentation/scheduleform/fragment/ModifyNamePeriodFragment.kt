package kr.tekit.lion.presentation.scheduleform.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentModifyNamePeriodBinding


class ModifyNamePeriodFragment : Fragment(R.layout.fragment_modify_name_period) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentModifyNamePeriodBinding.bind(view)
    }

}