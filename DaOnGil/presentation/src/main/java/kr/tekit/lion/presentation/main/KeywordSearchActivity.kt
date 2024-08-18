package kr.tekit.lion.presentation.main

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ActivityKeywordSearchBinding
import kr.tekit.lion.presentation.ext.repeatOnStarted
import kr.tekit.lion.presentation.main.adapter.RecentlyKeywordAdapter
import kr.tekit.lion.presentation.main.adapter.SearchSuggestionsAdapter
import kr.tekit.lion.presentation.main.dialog.ConfirmDialog
import kr.tekit.lion.presentation.main.vm.search.KeywordSearchViewModel

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

            },
            onClickDeleteBtn = {
                if (it != null) {
                    viewModel.deleteKeyword(it)
                }
            }
        )

        repeatOnStarted {
            viewModel.recentlySearchKeyword.collect{
                if (it.isEmpty()){
                    binding.rvRecentSearches.visibility = View.GONE
                    binding.tvNoSearch.visibility = View.VISIBLE
                }else{
                    binding.rvRecentSearches.visibility = View.VISIBLE
                    binding.tvNoSearch.visibility = View.GONE
                }
                recentlyKeywordAdapter.submitList(it)
            }
        }

        val searchAdapter = SearchSuggestionsAdapter{
            viewModel.insertKeyword(it)
        }

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val keywordLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)

        repeatOnStarted {
            viewModel.autocompleteKeyword.collect{
                searchAdapter.submitList(it.keywordList)
            }
        }

        with(binding){
            toolbar.setNavigationOnClickListener{
                finish()
            }
            searchSuggestions.adapter = searchAdapter
            searchSuggestions.layoutManager = layoutManager
            searchView.editText.doAfterTextChanged {
                viewModel.updateKeyword(it.toString())
            }

            rvRecentSearches.adapter = recentlyKeywordAdapter
            rvRecentSearches.layoutManager = keywordLayoutManager
            (rvRecentSearches.layoutManager as LinearLayoutManager).stackFromEnd = true

            tvDeleteAll.setOnClickListener {
                showDeleteConfirmDialog()
            }
        }
    }

    private fun showDeleteConfirmDialog() {
        val dialog = ConfirmDialog(
            "검색어 전체 삭제",
            "최근 검색어를 모두\n삭제하시겠습니까?",
            "삭제하기",
            R.color.button_tertiary,
            R.color.white
        ){
            viewModel.deleteAllKeyword()
        }
        dialog.isCancelable = false
        dialog.show(this.supportFragmentManager, "showDeleteConfirmDialog")
    }
}