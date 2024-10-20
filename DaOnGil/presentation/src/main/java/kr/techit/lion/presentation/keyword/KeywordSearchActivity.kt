package kr.techit.lion.presentation.keyword

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ActivityKeywordSearchBinding
import kr.techit.lion.presentation.ext.repeatOnStarted
import kr.techit.lion.presentation.keyword.fragment.SearchResultFragment
import kr.techit.lion.presentation.keyword.model.KeywordInputState
import kr.techit.lion.presentation.keyword.vm.KeywordSearchViewModel
import kr.techit.lion.presentation.observer.ConnectivityObserver
import kr.techit.lion.presentation.observer.NetworkConnectivityObserver

@AndroidEntryPoint
class KeywordSearchActivity : AppCompatActivity() {
    private val viewModel: KeywordSearchViewModel by viewModels()
    private val connectivityObserver: ConnectivityObserver by lazy {
        NetworkConnectivityObserver(applicationContext)
    }

    private lateinit var backPressedCallback: OnBackPressedCallback
    private val binding: ActivityKeywordSearchBinding by lazy {
        ActivityKeywordSearchBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        repeatOnStarted {
            supervisorScope {
                launch {
                    connectivityObserver.getFlow().collect { status ->
                        when (status) {
                            ConnectivityObserver.Status.Available -> {
                                viewModel.onChangeNetworkState(ConnectivityObserver.Status.Available)
                            }

                            ConnectivityObserver.Status.Losing -> {
                                viewModel.onChangeNetworkState(ConnectivityObserver.Status.Losing)
                            }

                            ConnectivityObserver.Status.Lost -> {
                                viewModel.onChangeNetworkState(ConnectivityObserver.Status.Lost)
                            }

                            ConnectivityObserver.Status.Unavailable -> {
                                viewModel.onChangeNetworkState(ConnectivityObserver.Status.Unavailable)
                            }
                        }
                    }
                }
            }
        }

        handleBackPress()
        settingSearchState()
    }

    private fun handleBackPress(){
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isSearchResultFragment()) {
                    binding.searchEdit.text = null
                }else{
                    finish()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    private fun settingSearchState() {
        with(binding) {
            toolbar.setNavigationOnClickListener {
                if (isSearchResultFragment()){
                    searchEdit.text = null
                }else {
                    finish()
                }
            }

            searchEdit.doAfterTextChanged {
                if (it.isNullOrEmpty()){
                    viewModel.keywordInputStateChanged(KeywordInputState.Empty)
                    if (isSearchResultFragment()) {
                        moveToBackStack()
                    }
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
                    val keyword = searchEdit.text.toString()
                    if (keyword.isEmpty()){
                        Snackbar.make(root, getString(R.string.hint_search), Snackbar.LENGTH_SHORT).show()
                    }else{
                        viewModel.insertKeyword(searchEdit.text.toString()) {
                            lifecycleScope.launch(Dispatchers.Main){
                                findNavController(R.id.fragment_container_view).navigate(
                                    R.id.action_to_searchResultFragment,
                                    bundleOf("searchText" to searchEdit.text.toString())
                                )
                            }
                        }
                    }
                    true
                } else {
                    false
                }
            }

            saerchBarContainer.setOnClickListener {
                searchEdit.text = null
                moveToBackStack()
            }
        }
    }

    private fun moveToBackStack() {
        findNavController(R.id.fragment_container_view).navigate(R.id.action_to_onSearchFragment)
    }

    private fun isSearchResultFragment(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        val currentFragment = navHostFragment.childFragmentManager.fragments[0]
        return currentFragment is SearchResultFragment
    }

    override fun onDestroy() {
        super.onDestroy()
        backPressedCallback.remove()
    }
}
