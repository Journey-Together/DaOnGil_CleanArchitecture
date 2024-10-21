package kr.techit.lion.presentation.bookmark

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.bookmark.adapter.PlaceBookmarkRVAdapter
import kr.techit.lion.presentation.bookmark.adapter.PlanBookmarkRVAdapter
import kr.techit.lion.presentation.bookmark.vm.BookmarkViewModel
import kr.techit.lion.presentation.databinding.ActivityBookmarkBinding
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.ext.repeatOnStarted
import kr.techit.lion.presentation.ext.showInfinitySnackBar
import kr.techit.lion.presentation.ext.showSnackbar
import kr.techit.lion.presentation.home.DetailActivity
import kr.techit.lion.presentation.observer.ConnectivityObserver
import kr.techit.lion.presentation.observer.NetworkConnectivityObserver
import kr.techit.lion.presentation.schedule.ScheduleDetailActivity

@AndroidEntryPoint
class BookmarkActivity : AppCompatActivity() {
    private val binding: ActivityBookmarkBinding by lazy {
        ActivityBookmarkBinding.inflate(layoutInflater)
    }
    private val viewModel: BookmarkViewModel by viewModels()
    private val connectivityObserver: ConnectivityObserver by lazy {
        NetworkConnectivityObserver(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        settingToolbar()
        settingTabLayout()
        settingPlaceBookmarkRVAdapter()
        observeSnackbarMsg()

        repeatOnStarted {
            supervisorScope {
                launch { collectBookmarkState() }
                launch { observeConnectivity() }
            }
        }
    }

    private suspend fun collectBookmarkState() {
        with(binding) {
            viewModel.networkState.collect { networkState ->
                when (networkState) {
                    is NetworkState.Loading -> {
                        bookmarkProgressBar.visibility = View.VISIBLE
                    }
                    is NetworkState.Success -> {
                        bookmarkProgressBar.visibility = View.GONE
                    }
                    is NetworkState.Error -> {
                        if(viewModel.isUpdateError.value == true) {
                            this@BookmarkActivity.showInfinitySnackBar(binding.root, networkState.msg)
                        } else {
                            bookmarkProgressBar.visibility = View.GONE
                            tabLayoutBookmark.visibility = View.GONE
                            bookmarkErrorLayout.visibility = View.VISIBLE
                            bookmarkErrorMsg.text = networkState.msg
                        }
                    }
                }
            }
        }
    }

    private suspend fun observeConnectivity() {
        with(binding) {
            connectivityObserver.getFlow().collect { connectivity ->
                when(connectivity) {
                    ConnectivityObserver.Status.Available -> {
                        tabLayoutBookmark.visibility = View.VISIBLE
                        recyclerViewBookmark.visibility = View.VISIBLE
                        bookmarkErrorLayout.visibility = View.GONE

                        if (viewModel.networkState.value is NetworkState.Error) {
                            viewModel.getPlaceBookmark()
                            viewModel.getPlanBookmark()
                        }
                    }
                    ConnectivityObserver.Status.Unavailable,
                    ConnectivityObserver.Status.Losing,
                    ConnectivityObserver.Status.Lost -> {
                        tabLayoutBookmark.visibility = View.GONE
                        recyclerViewBookmark.visibility = View.GONE
                        bookmarkErrorLayout.visibility = View.VISIBLE
                        val msg = "${getString(R.string.text_network_is_unavailable)}\n" +
                                "${getString(R.string.text_plz_check_network)} "
                        bookmarkErrorMsg.text = msg
                    }
                }
            }
        }
    }

    private fun observeSnackbarMsg() {
        viewModel.snackbarEvent.observe(this) { message ->
            message?.let {
                this@BookmarkActivity.showSnackbar(binding.root, it)
                viewModel.resetSnackbarEvent()
            }
        }
    }

    private fun settingToolbar() {
        binding.toolbarMyBookmark.setNavigationOnClickListener {
            finish()
        }

        binding.toolbarMyBookmark.setNavigationContentDescription(R.string.text_back_button)
    }

    private fun settingTabLayout() {
        binding.tabLayoutBookmark.addTab(binding.tabLayoutBookmark.newTab().setText(getString(R.string.tab_text_place)))
        binding.tabLayoutBookmark.addTab(binding.tabLayoutBookmark.newTab().setText(getString(R.string.tab_text_plan)))

        binding.tabLayoutBookmark.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> settingPlaceBookmarkRVAdapter()
                    1 -> settingPlanBookmarkRVAdapter()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding.tabLayoutBookmark.getTabAt(0)?.select()
    }

    private fun settingPlaceBookmarkRVAdapter() {
        viewModel.placeBookmarkList.observe(this) { placeBookmarkList ->
            if (placeBookmarkList.isNotEmpty()) {
                binding.notExistBookmarkLayout.visibility = View.INVISIBLE
                binding.recyclerViewBookmark.visibility = View.VISIBLE

                val placeBookmarkRVAdapter = PlaceBookmarkRVAdapter(
                    placeBookmarkList,
                    itemClickListener = { position ->
                        val placeBookmark = placeBookmarkList[position]
                        val intent = Intent(this, DetailActivity::class.java)
                        intent.putExtra("detailPlaceId", placeBookmark.placeId)
                        startActivity(intent)
                    },
                    onBookmarkClick = { placeId ->
                        viewModel.updatePlaceBookmark(placeId)
                    }
                )
                val rvState = binding.recyclerViewBookmark.layoutManager?.onSaveInstanceState()
                binding.recyclerViewBookmark.adapter = placeBookmarkRVAdapter
                rvState?.let {
                    binding.recyclerViewBookmark.layoutManager?.onRestoreInstanceState(it)
                }
            } else {
                binding.recyclerViewBookmark.visibility = View.INVISIBLE
                binding.notExistBookmarkLayout.visibility = View.VISIBLE
                binding.textViewNotExistBookmark.text = getString(R.string.text_place_bookmark)
            }
        }
    }

    private fun settingPlanBookmarkRVAdapter() {
        viewModel.planBookmarkList.observe(this) { planBookmarkList ->
            if (planBookmarkList.isNotEmpty()) {
                binding.notExistBookmarkLayout.visibility = View.INVISIBLE
                binding.recyclerViewBookmark.visibility = View.VISIBLE

                val planBookmarkRVAdapter = PlanBookmarkRVAdapter(
                    planBookmarkList,
                    itemClickListener = { position ->
                        val planBookmark = planBookmarkList[position]
                        val intent = Intent(this, ScheduleDetailActivity::class.java)
                        intent.putExtra("planId", planBookmark.planId)
                        startActivity(intent)
                    },
                    onBookmarkClick = { planId ->
                        viewModel.updatePlanBookmark(planId)
                    }
                )
                val rvState = binding.recyclerViewBookmark.layoutManager?.onSaveInstanceState()
                binding.recyclerViewBookmark.adapter = planBookmarkRVAdapter
                rvState?.let {
                    binding.recyclerViewBookmark.layoutManager?.onRestoreInstanceState(it)
                }
            } else {
                binding.recyclerViewBookmark.visibility = View.INVISIBLE
                binding.notExistBookmarkLayout.visibility = View.VISIBLE
                binding.textViewNotExistBookmark.text = getString(R.string.text_plan_bookmark)
            }
        }
    }
}