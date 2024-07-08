package kr.tekit.lion.presentation.main.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentSearchListMainBinding
import kr.tekit.lion.presentation.ext.repeatOnViewStarted
import kr.tekit.lion.presentation.main.CategoryBottomSheet
import kr.tekit.lion.presentation.main.adapter.ListSearchAdapter
import kr.tekit.lion.presentation.main.model.AreaModel
import kr.tekit.lion.presentation.main.model.CategoryModel
import kr.tekit.lion.presentation.main.model.DisabilityType
import kr.tekit.lion.presentation.main.model.ListSearchUIModel
import kr.tekit.lion.presentation.main.vm.SearchMainViewModel

@AndroidEntryPoint
class SearchListMainFragment : Fragment(R.layout.fragment_search_list_main) {
    private val viewModel: SearchMainViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSearchListMainBinding.bind(view)

        val mainAdapter = ListSearchAdapter(
            viewLifecycleOwner.lifecycleScope,
            onClickPhysicalDisability = { type->
                val options = viewModel.physicalDisabilityOptions.value
                showBottomSheet(options, type)
            },
            onClickVisualImpairment = { type ->
                val options = viewModel.visualImpairmentOptions.value
                showBottomSheet(options, type)
            },
            onClickHearingDisability = { type->
                val options = viewModel.hearingImpairmentOptions.value
                showBottomSheet(options, type)
            },
            onClickInfantFamily = { type->
                val options = viewModel.infantFamilyOptions.value
                showBottomSheet(options, type)
            },
            onClickElderlyPeople = { type ->
                val options = viewModel.elderlyPersonOptions.value
                showBottomSheet(options, type)
            },
            onSelectArea = {
                viewModel.onSelectedArea(it)
            },
            onSelectSigungu = {
                viewModel.onSelectedSigungu(it)
            },
            onClickSearchButton = {
                viewModel.onClickSearchButton()
            },
            onClickSortByLatestBtn = {
                viewModel.onSelectedArrange(it)
            },
            onClickSortByLetterBtn = {
                viewModel.onSelectedArrange(it)
            },
            onClickSortByPopularityBtn = {
                viewModel.onSelectedArrange(it)
            }
        )

        val arr = ArrayList<ListSearchUIModel>()
        arr.add(CategoryModel)
        arr.add(AreaModel)

        mainAdapter.submitList(arr)
        val layoutManager = GridLayoutManager(requireContext(), 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (mainAdapter.getItemViewType(position)) {
                    ListSearchAdapter.VIEW_TYPE_PLACE -> 1
                    else -> 2
                }
            }
        }

        binding.rvSearchResult.adapter = mainAdapter
        binding.rvSearchResult.layoutManager = layoutManager

        repeatOnViewStarted {
            viewModel.areaCode.collect {area->
                mainAdapter.submitAreaList(area.map { it.name })
            }
        }

        repeatOnViewStarted {
            viewModel.sigunguCode.collect { result ->
                mainAdapter.submitSigunguList(
                    result.sigunguList.map {
                        it.sigunguName
                    }
                )
            }
        }

        repeatOnViewStarted {
            viewModel.place.collect{
                mainAdapter.submitList(it)
            }
        }

        repeatOnViewStarted {
            viewModel.optionState.collect{
                mainAdapter.modifyOptionState(it)
            }
        }
    }

    private fun showBottomSheet(selectedOptions: List<Int>, disabilityType: DisabilityType) {
        CategoryBottomSheet(selectedOptions, disabilityType) { optionIds, optionNames ->
            viewModel.onSelectOption(optionIds, optionNames, disabilityType)
        }.show(parentFragmentManager, "bottomSheet")
    }
}

