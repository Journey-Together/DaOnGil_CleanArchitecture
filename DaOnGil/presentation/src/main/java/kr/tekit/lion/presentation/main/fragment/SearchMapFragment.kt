package kr.tekit.lion.presentation.main.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentSearchMapBinding

@AndroidEntryPoint
class SearchMapFragment : Fragment(R.layout.fragment_search_map) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSearchMapBinding.bind(view)
    }

}