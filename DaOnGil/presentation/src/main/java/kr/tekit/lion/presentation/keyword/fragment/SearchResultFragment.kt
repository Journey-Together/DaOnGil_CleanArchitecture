package kr.tekit.lion.presentation.keyword.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentSearchResultBinding
import kr.tekit.lion.presentation.ext.addOnScrollEndListener
import kr.tekit.lion.presentation.ext.repeatOnViewStarted
import kr.tekit.lion.presentation.keyword.adapter.SearchResultAdapter
import kr.tekit.lion.presentation.keyword.vm.SearchResultViewModel

@AndroidEntryPoint
class SearchResultFragment : Fragment(R.layout.fragment_search_result) {
    private val viewModel: SearchResultViewModel by viewModels({ requireActivity() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSearchResultBinding.bind(view)

        val searchText = arguments?.getString("searchText")
        searchText?.let {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.onChangeQuery(searchText)
            }
        }

        val rvAdapter = SearchResultAdapter{

        }

        with(binding.searchResultRv) {
            adapter = rvAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            addOnScrollEndListener {
                val pageState = viewModel.isLastPage.value
                if (pageState.not()) {
                    viewModel.whenLastPageReached()
                }
            }
        }

        repeatOnViewStarted {
            viewModel.place.collect{
                if (it.isEmpty()){
                    binding.noSearchResultContainer.visibility = View.VISIBLE
                    binding.searchResultRv.visibility = View.GONE
                }else{
                    binding.noSearchResultContainer.visibility = View.GONE
                    binding.searchResultRv.visibility = View.VISIBLE
                    rvAdapter.submitList(it)
                }
            }
        }
    }
}