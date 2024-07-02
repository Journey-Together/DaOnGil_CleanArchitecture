package kr.tekit.lion.presentation.main.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentSearchListMainBinding

class SearchListMainFragment : Fragment(R.layout.fragment_search_list_main) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSearchListMainBinding.bind(view)
    }
}

