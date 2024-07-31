package kr.tekit.lion.presentation.main.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentSearchListBinding
import kr.tekit.lion.presentation.ext.addOnScrollEndListener
import kr.tekit.lion.presentation.ext.repeatOnViewStarted
import kr.tekit.lion.presentation.main.adapter.ListSearchAdapter
import kr.tekit.lion.presentation.main.bottomsheet.CategoryBottomSheet
import kr.tekit.lion.presentation.main.model.AreaModel
import kr.tekit.lion.presentation.main.model.CategoryModel
import kr.tekit.lion.presentation.main.model.DisabilityType
import kr.tekit.lion.presentation.main.model.ListSearchUIModel
import kr.tekit.lion.presentation.main.model.PlaceModel
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
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.onSelectedArea(it)
                }
            },
            onSelectSigungu = {
                viewModel.onSelectedSigungu(it)
            },
            onClickSortByLatestBtn = {
                viewModel.onSelectedArrange(it)
            },
            onClickSortByLetterBtn = {
                viewModel.onSelectedArrange(it)
            },
            onClickSortByPopularityBtn = {
                viewModel.onSelectedArrange(it)
            })

        val arr = ArrayList<ListSearchUIModel>()
        arr.add(CategoryModel)
        arr.add(AreaModel)

        mainAdapter.submitList(arr)
        val layoutManager = GridLayoutManager(requireContext(), 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (mainAdapter.getItemViewType(position)) {
                    PlaceModel().id -> 1
                    else -> 2
                }
            }
        }

        with(binding) {
            rvSearchResult.adapter = mainAdapter
            rvSearchResult.layoutManager = layoutManager
            rvSearchResult.addOnScrollEndListener {
                val pageState = viewModel.isLastPage.value
                if (pageState.not()){
                    viewModel.whenLastPageReached()
                }
            }
        }
        repeatOnViewStarted {
            sharedViewModel.sharedOptionState.collect{
                viewModel.onChangeMapState(it)
            }
        }

        repeatOnViewStarted {
            sharedViewModel.tabState.collect{
                binding.rvSearchResult.scrollToPosition(0)
                viewModel.onSelectedTab(it)
            }
        }

        repeatOnViewStarted {
            viewModel.areaCode.collect { area ->
                mainAdapter.submitAreaList(area.areaList.map { it.name })
            }
        }

        repeatOnViewStarted {
            viewModel.sigunguCode.collect { result ->
                mainAdapter.submitSigunguList(result.sigunguList.map {
                    it.sigunguName
                })
            }
        }

        repeatOnViewStarted {
            viewModel.place.collect {
                mainAdapter.submitList(it)
            }
        }

        repeatOnViewStarted {
            sharedViewModel.bottomSheetOptionState.collect {
                mainAdapter.modifyOptionState(it)
            }
        }

        repeatOnViewStarted {
            viewModel.errorMessage.collect { msg ->
                msg?.let { mainAdapter.submitErrorMessage(it) }
            }
        }
    }

    private fun showBottomSheet(selectedOptions: List<Int>, disabilityType: DisabilityType) {
        CategoryBottomSheet(selectedOptions, disabilityType) { optionIds, optionNames ->
            sharedViewModel.onSelectOption(optionIds, disabilityType, optionNames)
            viewModel.onSelectOption(optionNames, disabilityType)
        }.show(parentFragmentManager, "bottomSheet")
    }

    fun updateData(data: Boolean) {
        if (data) viewModel.onMapChanged(data)
    }
}

