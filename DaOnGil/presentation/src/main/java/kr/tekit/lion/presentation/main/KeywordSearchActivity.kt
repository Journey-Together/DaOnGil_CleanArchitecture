package kr.tekit.lion.presentation.main

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.search.SearchBar
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ActivityKeywordSearchBinding
import kr.tekit.lion.presentation.ext.repeatOnStarted
import kr.tekit.lion.presentation.main.adapter.SearchSuggestionsAdapter
import kr.tekit.lion.presentation.main.vm.search.KeywordSearchViewModel

@AndroidEntryPoint
class KeywordSearchActivity : AppCompatActivity() {
    private val viewModel: KeywordSearchViewModel by viewModels()
    private val binding: ActivityKeywordSearchBinding by lazy {
        ActivityKeywordSearchBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val searchAdapter = SearchSuggestionsAdapter{

        }
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        repeatOnStarted {
            viewModel.autocompleteKeyword.collect{
                searchAdapter.submitList(it.keywordList)
            }
        }

        with(binding){
            searchSuggestions.adapter = searchAdapter
            searchSuggestions.layoutManager = layoutManager
            searchView.editText.doAfterTextChanged {
                viewModel.updateKeyword(it.toString())

            }
        }


    }
}