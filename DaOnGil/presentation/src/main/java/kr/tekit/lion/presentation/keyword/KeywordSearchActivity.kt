package kr.tekit.lion.presentation.keyword

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ActivityKeywordSearchBinding
import kr.tekit.lion.presentation.ext.repeatOnStarted
import kr.tekit.lion.presentation.keyword.vm.KeywordSearchViewModel
import kr.tekit.lion.presentation.main.adapter.RecentlyKeywordAdapter
import kr.tekit.lion.presentation.main.adapter.SearchSuggestionsAdapter
import kr.tekit.lion.presentation.main.dialog.ConfirmDialog

@AndroidEntryPoint
class KeywordSearchActivity : AppCompatActivity() {
    private val viewModel: KeywordSearchViewModel by viewModels()
    private val binding: ActivityKeywordSearchBinding by lazy {
        ActivityKeywordSearchBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val recentlyKeywordAdapter = RecentlyKeywordAdapter(
            onClick = {
                // 화면 이동 구현할것
                viewModel.onClickSearchButton(it)
            },
            onClickDeleteBtn = { keywordId ->
                keywordId?.let { viewModel.deleteKeyword(it) }
            }
        )

        val searchAdapter = SearchSuggestionsAdapter{
            viewModel.onClickSearchButton(it)
            viewModel.insertKeyword(it)
        }

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val keywordLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)

        with(binding){
            toolbar.setNavigationOnClickListener{
                finish()
            }
            searchSuggestions.adapter = searchAdapter
            searchSuggestions.layoutManager = layoutManager
            searchEdit.doAfterTextChanged {
                searchRecentSearchesContainer.visibility = if (it.isNullOrEmpty()) View.VISIBLE else View.GONE
                if (!it.isNullOrEmpty()) viewModel.updateKeyword(it.toString())
            }

            rvRecentSearches.adapter = recentlyKeywordAdapter
            rvRecentSearches.layoutManager = keywordLayoutManager
            (rvRecentSearches.layoutManager as LinearLayoutManager).stackFromEnd = true

            tvDeleteAll.setOnClickListener {
                showDeleteConfirmDialog()
            }
        }

        repeatOnStarted {
            supervisorScope {
                launch {
                    viewModel.recentlySearchKeyword.collect {
                        if (it.isEmpty()) {
                            binding.rvRecentSearches.visibility = View.GONE
                            binding.tvNoSearch.visibility = View.VISIBLE
                        } else {
                            binding.rvRecentSearches.visibility = View.VISIBLE
                            binding.tvNoSearch.visibility = View.GONE
                        }
                        recentlyKeywordAdapter.submitList(it)
                    }
                }

                launch {
                    viewModel.errorMessage
                        .filter { it != null }
                        .collect {
                            binding.textMsg.text = it
                            binding.noSearchResultContainer.visibility = View.VISIBLE
                            binding.searchRecentSearchesContainer.visibility = View.GONE
                            binding.searchSuggestions.visibility = View.GONE
                        }
                }

                launch {
                    repeatOnStarted {
                        viewModel.autocompleteKeyword.collectLatest {
                            searchAdapter.submitList(it.keywordList)
                            setNoSearchResultVisibility(
                                it.keywordList.isEmpty() && binding.searchEdit.text.toString().isNotEmpty()
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setNoSearchResultVisibility(isVisible: Boolean) {
        binding.noSearchResultContainer.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun showDeleteConfirmDialog() {
        val dialog = ConfirmDialog(
            "검색어 전체 삭제",
            "최근 검색어를 모두\n삭제하시겠습니까?",
            "삭제하기",
        ){
            viewModel.deleteAllKeyword()
        }
        dialog.isCancelable = false
        dialog.show(this.supportFragmentManager, "showDeleteConfirmDialog")
    }
}