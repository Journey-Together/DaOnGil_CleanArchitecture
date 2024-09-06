package kr.tekit.lion.presentation.keyword.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentOnSearchBinding
import kr.tekit.lion.presentation.ext.repeatOnStarted
import kr.tekit.lion.presentation.home.DetailActivity
import kr.tekit.lion.presentation.keyword.adapter.SearchSuggestionsAdapter
import kr.tekit.lion.presentation.keyword.model.KeywordInputState
import kr.tekit.lion.presentation.keyword.vm.KeywordSearchViewModel
import kr.tekit.lion.presentation.main.adapter.RecentlyKeywordAdapter
import kr.tekit.lion.presentation.main.dialog.ConfirmDialog

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
                            if (it.isEmpty()) {
                                rvRecentSearches.visibility = View.GONE
                                tvNoSearch.visibility = View.VISIBLE
                            } else {
                                rvRecentSearches.visibility = View.VISIBLE
                                tvNoSearch.visibility = View.GONE
                            }
                            recentlyKeywordAdapter.submitList(it)
                        }
                    }

                    launch {
                        viewModel.errorMessage.filter { it != null }.collect {
                            textMsg.text = it
                            noSearchResultContainer.visibility = View.VISIBLE
                            searchRecentSearchesContainer.visibility = View.GONE
                            searchSuggestions.visibility = View.GONE
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