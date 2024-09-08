package kr.tekit.lion.presentation.scheduleform.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentFormSearchBinding

@AndroidEntryPoint
class ModifySearchFragment : Fragment(R.layout.fragment_form_search) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentFormSearchBinding.bind(view)
    }

}