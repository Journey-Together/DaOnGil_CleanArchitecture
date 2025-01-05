package kr.techit.lion.presentation.keyword.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.FragmentSearchResultBinding
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.ext.addOnScrollEndListener
import kr.techit.lion.presentation.ext.hideSoftInput
import kr.techit.lion.presentation.ext.repeatOnViewStarted
import kr.techit.lion.presentation.home.DetailActivity
import kr.techit.lion.presentation.keyword.adapter.SearchResultAdapter
import kr.techit.lion.presentation.keyword.vm.SearchResultViewModel
import kr.techit.lion.presentation.observer.ConnectivityObserver
import kr.techit.lion.presentation.observer.NetworkConnectivityObserver

@AndroidEntryPoint
class SearchResultFragment : Fragment(R.layout.fragment_search_result) {
    private val viewModel: SearchResultViewModel by viewModels()
    private val connectivityObserver: ConnectivityObserver by lazy {
        NetworkConnectivityObserver(requireContext().applicationContext)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSearchResultBinding.bind(view)

        requireContext().hideSoftInput(binding.noSearchResultContainer)

        val searchText = arguments?.getString("searchText")
        searchText?.let {
            requireActivity().findViewById<TextInputEditText>(R.id.search_edit).setText(searchText)
        }

        val rvAdapter = SearchResultAdapter {
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("detailPlaceId", it)
            startActivity(intent)
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
            supervisorScope {
                launch {
                    combine(viewModel.networkState, viewModel.place) { networkState, place ->
                        val progressBar =
                            requireActivity().findViewById<ProgressBar>(R.id.search_view_progressBar)
                        when (networkState) {
                            is NetworkState.Loading -> {
                                progressBar.visibility = View.VISIBLE
                            }

                            is NetworkState.Success -> {
                                progressBar.visibility = View.GONE
                                if (place.isEmpty()) {
                                    binding.noSearchResultContainer.visibility = View.VISIBLE
                                    binding.searchResultRv.visibility = View.GONE
                                } else {
                                    binding.noSearchResultContainer.visibility = View.GONE
                                    binding.searchResultRv.visibility = View.VISIBLE
                                    rvAdapter.submitList(place)
                                }
                            }

                            is NetworkState.Error -> {
                                progressBar.visibility = View.GONE
                                binding.noSearchResultContainer.visibility = View.VISIBLE
                                binding.textMsg.text = networkState.msg
                            }
                        }
                    }.collect { }
                }
                launch {
                    connectivityObserver.getFlow().collect { status ->
                        if (status == ConnectivityObserver.Status.Available) {
                            searchText?.let { viewModel.onChangeQuery(searchText) }
                        }
                    }
                }
            }
        }
    }
}