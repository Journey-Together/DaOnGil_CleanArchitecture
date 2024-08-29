package kr.tekit.lion.presentation.bookmark

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.bookmark.adapter.PlaceBookmarkRVAdapter
import kr.tekit.lion.presentation.bookmark.adapter.PlanBookmarkRVAdapter
import kr.tekit.lion.presentation.bookmark.vm.BookmarkViewModel
import kr.tekit.lion.presentation.databinding.ActivityBookmarkBinding

@AndroidEntryPoint
class BookmarkActivity : AppCompatActivity() {
    private val binding: ActivityBookmarkBinding by lazy {
        ActivityBookmarkBinding.inflate(layoutInflater)
    }

    private val viewModel: BookmarkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        settingToolbar()
        settingTabLayout()
        settingPlaceBookmarkRVAdapter()
    }

    private fun settingToolbar() {
        binding.toolbarMyBookmark.setNavigationOnClickListener {
            finish()
        }
    }

    private fun settingTabLayout() {
        binding.tabLayoutBookmark.addTab(binding.tabLayoutBookmark.newTab().setText(getString(R.string.tab_text_place)))
        binding.tabLayoutBookmark.addTab(binding.tabLayoutBookmark.newTab().setText(getString(R.string.tab_text_plan)))

        binding.tabLayoutBookmark.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> settingPlaceBookmarkRVAdapter()
                    1 -> settingScheduleBookmarkRVAdapter()
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
//                        val placeBookmark = placeBookmarkList[position]
//                        val intent = Intent(this, DetailActivity::class.java)
//                        intent.putExtra("detailPlaceId", placeBookmark.placeId)
//                        startActivity(intent)
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

    private fun settingScheduleBookmarkRVAdapter() {
        viewModel.planBookmarkList.observe(this) { planBookmarkList ->
            if (planBookmarkList.isNotEmpty()) {
                binding.notExistBookmarkLayout.visibility = View.INVISIBLE
                binding.recyclerViewBookmark.visibility = View.VISIBLE

                val planBookmarkRVAdapter = PlanBookmarkRVAdapter(
                    planBookmarkList,
                    itemClickListener = { position ->
//                        val planBookmark = planBookmarkList[position]
//                        val intent = Intent(this, ScheduleDetailActivity::class.java)
//                        intent.putExtra("planId", planBookmark.planId)
//                        startActivity(intent)
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