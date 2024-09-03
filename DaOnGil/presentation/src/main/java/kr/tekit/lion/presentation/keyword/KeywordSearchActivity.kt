package kr.tekit.lion.presentation.keyword

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ActivityKeywordSearchBinding
import kr.tekit.lion.presentation.keyword.fragment.OnSearchFragment
import kr.tekit.lion.presentation.keyword.fragment.SearchResultFragment
import kr.tekit.lion.presentation.keyword.model.KeywordInputState
import kr.tekit.lion.presentation.keyword.vm.KeywordSearchViewModel

@AndroidEntryPoint
class KeywordSearchActivity : AppCompatActivity() {
    private val viewModel: KeywordSearchViewModel by viewModels()
    private val binding: ActivityKeywordSearchBinding by lazy {
        ActivityKeywordSearchBinding.inflate(layoutInflater)
    }
    private val onSearchFragment by lazy { OnSearchFragment() }
    private val searchResultFragment by lazy { SearchResultFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragment_container_view, onSearchFragment, OnSearchFragment::class.java.name)
            commit()
        }

        with(binding) {
            toolbar.setNavigationOnClickListener {
                finish()
            }

            searchEdit.doAfterTextChanged {
                if (it.isNullOrEmpty()){
                    viewModel.keywordInputStateChanged(KeywordInputState.Empty)
                    moveToBackStack()
                }else if (it.length >= 2) {
                    viewModel.inputTextChanged(it.toString())
                    viewModel.keywordInputStateChanged(KeywordInputState.NotEmpty)
                }else if (it.length < 2){
                    viewModel.keywordInputStateChanged(KeywordInputState.Erasing)
                }
            }

            searchEdit.setOnEditorActionListener { _, actionId, event ->
                val isImeActionDone = (event == null && actionId == EditorInfo.IME_ACTION_DONE)
                val isEnterKeyPressed = (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)

                if (isImeActionDone || isEnterKeyPressed) {
                    viewModel.insertKeyword(searchEdit.text.toString()) {
                        showFragment(searchResultFragment)
                    }
                    true
                } else {
                    false
                }
            }

            saerchBarContainer.setOnClickListener {
                moveToBackStack()
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        if (fragment is SearchResultFragment) {
            val bundle = Bundle()
            bundle.putString("searchText", binding.searchEdit.text.toString())
            fragment.arguments = bundle
        }

        transaction.replace(R.id.fragment_container_view, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun moveToBackStack(){
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view)
        if (currentFragment is SearchResultFragment) {
            onBackPressed()
        }
    }
}
