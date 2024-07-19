package kr.tekit.lion.presentation.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentSearchPlaceMainBinding
import kr.tekit.lion.presentation.ext.repeatOnViewStarted
import kr.tekit.lion.presentation.main.model.Category
import kr.tekit.lion.presentation.main.model.ScreenState
import kr.tekit.lion.presentation.main.vm.SearchMainViewModel

@AndroidEntryPoint
class SearchPlaceMainFragment : Fragment(R.layout.fragment_search_place_main) {
    private val viewModel: SearchMainViewModel by viewModels()
    private lateinit var listFragment: SearchListMainFragment
    private lateinit var mapFragment: SearchMapFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSearchPlaceMainBinding.bind(view)

        // 프래그먼트 초기화 및 추가
        listFragment = SearchListMainFragment()
        mapFragment = SearchMapFragment()

        childFragmentManager.beginTransaction().apply {
            add(R.id.fragmentContainerView, listFragment, ScreenState.List.name)
            add(R.id.fragmentContainerView, mapFragment, ScreenState.Map.name)
            hide(mapFragment)
            commit()
        }

        with(binding) {
            tabContainer.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    // 탭이 선택되었을 때 수행할 작업
                    when (tab.position) {
                        0 -> viewModel.onSelectedTab(Category.PLACE)
                        1 -> viewModel.onSelectedTab(Category.RESTAURANT)
                        2 -> viewModel.onSelectedTab(Category.ROOM)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabReselected(tab: TabLayout.Tab) {}
            })

            modeSwitchBtn.setOnClickListener {
                when (viewModel.screenState.value) {
                    ScreenState.List -> viewModel.changeScreenState(ScreenState.Map)
                    ScreenState.Map -> viewModel.changeScreenState(ScreenState.List)
                }
            }

            repeatOnViewStarted {
                viewModel.screenState.collect {
                    when (it) {
                        ScreenState.Map -> {
                            showFragment(mapFragment)
                            hideFragment(listFragment)
                            modeSwitchBtn.setText(R.string.watching_list)
                            modeSwitchBtn.setIconResource(R.drawable.list_icon)
                        }

                        ScreenState.List -> {
                            showFragment(listFragment)
                            hideFragment(mapFragment)
                            modeSwitchBtn.setText(R.string.watching_map)
                            modeSwitchBtn.setIconResource(R.drawable.map_icon)
                        }
                    }
                }
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().apply {
            show(fragment)
            commit()
        }
    }

    private fun hideFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().apply {
            hide(fragment)
            commit()
        }
    }
}