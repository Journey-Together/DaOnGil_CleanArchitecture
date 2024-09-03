package kr.tekit.lion.presentation.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentSearchListBinding
import kr.tekit.lion.presentation.ext.addOnScrollEndListener
import kr.tekit.lion.presentation.ext.repeatOnViewStarted
import kr.tekit.lion.presentation.main.adapter.ListSearchAdapter
import kr.tekit.lion.presentation.main.adapter.ListSearchAdapter.Companion.VIEW_TYPE_PLACE
import kr.tekit.lion.presentation.main.bottomsheet.CategoryBottomSheet
import kr.tekit.lion.presentation.main.model.AreaModel
import kr.tekit.lion.presentation.main.model.DisabilityType
import kr.tekit.lion.presentation.main.vm.search.SearchListViewModel
import kr.tekit.lion.presentation.main.vm.search.SharedViewModel

@AndroidEntryPoint
class SearchListFragment : Fragment(R.layout.fragment_search_list) {
    private val sharedViewModel: SharedViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private val viewModel: SearchListViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSearchListBinding.bind(view)

        val mainAdapter = ListSearchAdapter(viewLifecycleOwner.lifecycleScope,
            onClickPhysicalDisability = { type ->
                val options = sharedViewModel.physicalDisabilityOptions.value
                showBottomSheet(options, type)
            },
            onClickVisualImpairment = { type ->
                val options = sharedViewModel.visualImpairmentOptions.value
                showBottomSheet(options, type)
            },
            onClickHearingDisability = { type ->
                val options = sharedViewModel.hearingImpairmentOptions.value
                showBottomSheet(options, type)
            },
            onClickInfantFamily = { type ->
                val options = sharedViewModel.infantFamilyOptions.value
                showBottomSheet(options, type)
            },
            onClickElderlyPeople = { type ->
                val options = sharedViewModel.elderlyPersonOptions.value
                showBottomSheet(options, type)
            },
            onSelectArea = {
                repeatOnViewStarted {
                    viewModel.onSelectedArea(it)
                }
            },
            onSelectSigungu = {
                viewModel.onSelectedSigungu(it)
            },
            onSelectPlace = {

            }
        )

        val rvLayoutManager = GridLayoutManager(requireContext(), 2)
        rvLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (mainAdapter.getItemViewType(position)) {
                    VIEW_TYPE_PLACE -> 1
                    else -> 2
                }
            }
        }

        val placeViewPool = RecyclerView.RecycledViewPool().apply {
            setMaxRecycledViews(VIEW_TYPE_PLACE, 10)
        }

        with(binding.rvSearchResult) {
            adapter = mainAdapter
            itemAnimator = null
            layoutManager = rvLayoutManager

            setRecycledViewPool(placeViewPool)
            addOnScrollEndListener {
                val pageState = viewModel.isLastPage.value
                if (pageState.not()) {
                    viewModel.whenLastPageReached()
                }
            }
        }
        repeatOnViewStarted {
            supervisorScope {
                launch {
                    sharedViewModel.sharedOptionState.collect {
                        viewModel.onChangeMapState(it)
                    }
                }

                launch {
                    sharedViewModel.tabState.collect {
                        binding.rvSearchResult.scrollToPosition(0)
                        viewModel.onSelectedTab(it)
                    }
                }

                launch {
                    viewModel.uiState
                        .filter { uiState ->
                            uiState.any { it is AreaModel && it.areas.isNotEmpty() }
                        }.collect {
                            mainAdapter.submitList(it)
                        }
                }

                launch {
                    sharedViewModel.bottomSheetOptionState.collect {
                        viewModel.modifyCategoryModel(it)
                    }
                }

                launch {
                    viewModel.errorMessage
                        .filter { it != null }
                        .collect { msg ->
                            //msg?.let { mainAdapter.submitList(it) }
                        }
                }
            }
        }
    }

    private fun showBottomSheet(selectedOptions: List<Int>, disabilityType: DisabilityType) {
        CategoryBottomSheet(selectedOptions, disabilityType) { optionIds, optionNames ->
            sharedViewModel.onSelectOption(optionIds, disabilityType, optionNames)
            viewModel.onSelectOption(optionNames, disabilityType)
        }.show(parentFragmentManager, "bottomSheet")
    }

    fun mapChanged(state: Boolean) {
        viewModel.onMapChanged(state)
    }
}