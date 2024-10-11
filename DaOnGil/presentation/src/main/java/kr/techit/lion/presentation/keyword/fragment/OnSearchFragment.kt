package kr.techit.lion.presentation.keyword.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.FragmentOnSearchBinding
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.ext.repeatOnStarted
import kr.techit.lion.presentation.home.DetailActivity
import kr.techit.lion.presentation.keyword.adapter.SearchSuggestionsAdapter
import kr.techit.lion.presentation.keyword.model.KeywordInputState
import kr.techit.lion.presentation.keyword.vm.KeywordSearchViewModel
import kr.techit.lion.presentation.main.adapter.RecentlyKeywordAdapter
import kr.techit.lion.presentation.main.dialog.ConfirmDialog
import kr.techit.lion.presentation.observer.ConnectivityObserver

@AndroidEntryPoint
class OnSearchFragment : Fragment(R.layout.fragment_on_search) {
    private val viewModel: KeywordSearchViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentOnSearchBinding.bind(view)

        val recentlyKeywordAdapter = RecentlyKeywordAdapter(
            onClick = {
                findNavController().navigate(
                    R.id.action_to_searchResultFragment,
                    bundleOf("searchText" to it)
                )
            },
            onClickDeleteBtn = { keywordId ->
                keywordId?.let { viewModel.deleteKeyword(it) }
            }
        )

        val searchAdapter = SearchSuggestionsAdapter {
            viewModel.insertKeyword(it.keyword) {
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra("detailPlaceId", it.placeId)
                startActivity(intent)
            }
        }

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val keywordLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)

        with(binding) {
            searchSuggestions.adapter = searchAdapter
            searchSuggestions.layoutManager = layoutManager

            rvRecentSearches.adapter = recentlyKeywordAdapter
            rvRecentSearches.layoutManager = keywordLayoutManager
            (rvRecentSearches.layoutManager as LinearLayoutManager).stackFromEnd = true

            tvDeleteAll.setOnClickListener {
                showDeleteConfirmDialog()
            }
        }

        with(binding) {
            repeatOnStarted {
                supervisorScope {
                    launch {
                        viewModel.recentlySearchKeyword.collect {
                            if (it.list.isEmpty()) {
                                rvRecentSearches.visibility = View.GONE
                                tvNoSearch.visibility = View.VISIBLE
                            } else {
                                rvRecentSearches.visibility = View.VISIBLE
                                tvNoSearch.visibility = View.GONE
                            }
                            recentlyKeywordAdapter.submitList(it.list)
                        }
                    }

                    launch {
                        viewModel.searchState.collect { state ->
                            when (state) {
                                KeywordInputState.Initial -> {
                                    searchRecentSearchesContainer.visibility = View.VISIBLE
                                    searchSuggestions.visibility = View.GONE
                                    noSearchResultContainer.visibility = View.GONE
                                }

                                KeywordInputState.NotEmpty -> {
                                    if (viewModel.networkStatus.value != ConnectivityObserver.Status.Available){
                                        textMsg.text = "서버에 연결할 수 없어요 \n 인터넷 연결을 확인해주세요."
                                        noSearchResultContainer.visibility = View.VISIBLE
                                        searchRecentSearchesContainer.visibility = View.GONE
                                        searchSuggestions.visibility = View.GONE
                                    }
                                    searchRecentSearchesContainer.visibility = View.GONE
                                }

                                KeywordInputState.Empty -> {
                                    viewModel.keywordInputStateChanged(KeywordInputState.Initial)
                                }

                                KeywordInputState.Erasing -> {
                                    return@collect
                                }
                            }
                        }
                    }

                    launch {
                        viewModel.autocompleteKeyword.collect { suggestKeywords ->
                            if (suggestKeywords.isEmpty() &&
                                viewModel.searchState.value == KeywordInputState.NotEmpty
                            ) {
                                noSearchResultContainer.visibility = View.VISIBLE
                            } else {
                                searchSuggestions.visibility = View.VISIBLE
                                noSearchResultContainer.visibility = View.GONE
                                searchAdapter.submitList(suggestKeywords)
                            }
                        }
                    }

                    launch {
                        viewModel.errorState.collect {
                            when (it) {
                                is NetworkState.Loading, NetworkState.Success -> {
                                    searchSuggestions.visibility = View.VISIBLE
                                }
                                is NetworkState.Error -> {
                                    textMsg.text = it.msg
                                    noSearchResultContainer.visibility = View.VISIBLE
                                    searchRecentSearchesContainer.visibility = View.GONE
                                    searchSuggestions.visibility = View.GONE
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showDeleteConfirmDialog() {
        val dialog = ConfirmDialog(
            "검색어 전체 삭제",
            "최근 검색어를 모두\n삭제하시겠습니까?",
            "삭제하기",
        ) {
            viewModel.onClickDeleteButton()
        }
        dialog.isCancelable = false
        dialog.show(childFragmentManager, "showDeleteConfirmDialog")
    }

    override fun onResume() {
        super.onResume()
        viewModel.inputTextChanged("")
    }
}