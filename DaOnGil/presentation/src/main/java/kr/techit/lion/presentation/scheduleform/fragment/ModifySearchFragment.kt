package kr.techit.lion.presentation.scheduleform.fragment

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.FragmentFormSearchBinding
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.ext.addOnScrollEndListener
import kr.techit.lion.presentation.ext.showSnackbar
import kr.techit.lion.presentation.home.DetailActivity
import kr.techit.lion.presentation.scheduleform.adapter.FormBookmarkedPlacesAdapter
import kr.techit.lion.presentation.scheduleform.adapter.FormSearchResultAdapter
import kr.techit.lion.presentation.scheduleform.vm.ModifyScheduleFormViewModel

@AndroidEntryPoint
class ModifySearchFragment : Fragment(R.layout.fragment_form_search) {
    private val args: ModifySearchFragmentArgs by navArgs()
    private val viewModel: ModifyScheduleFormViewModel by activityViewModels()

    private val searchResultAdapter by lazy {
        FormSearchResultAdapter(
            onPlaceSelectedListener = { selectedPlacePosition ->
                addNewPlace(args.schedulePosition, selectedPlacePosition, false)
            },
            onItemClickListener = { selectedPlacePosition ->
                val placeId = viewModel.getPlaceId(selectedPlacePosition)
                if(placeId != -1L){
                    navigateToPlaceDetail(placeId)
                }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val schedulePosition = args.schedulePosition

        val binding = FragmentFormSearchBinding.bind(view)

        settingProgressBarVisibility(binding)
        initBookmarkList()

        initToolbar(binding)
        settingBookmarkRV(binding, schedulePosition)
        settingSearchResultRV(binding)
        settingPlaceSearchView(binding)
    }

    private fun initBookmarkList(){
        viewModel.initBookmarkList()
    }

    private fun settingProgressBarVisibility(binding: FragmentFormSearchBinding){
        with(binding) {
            lifecycleScope.launch {
                viewModel.networkState.collectLatest { state ->
                    val searchViewVisibility = searchViewFsResult.isShowing

                    when(state) {
                        is NetworkState.Loading -> {
                            if(searchViewVisibility){
                                textFsResultError.visibility = View.GONE
                                progressBarFsResult.visibility = View.VISIBLE
                            }else{
                                progressBarFsBookmark.visibility = View.VISIBLE
                            }
                        }
                        is NetworkState.Success -> {
                            if(searchViewVisibility){
                                // 이전에 오류가 난 경우를 대비하여 에러메시지도 숨김처리
                                textFsResultError.visibility = View.GONE
                                progressBarFsResult.visibility = View.GONE
                            }else{
                                progressBarFsBookmark.visibility = View.GONE
                                recyclerViewFsBookmark.visibility = View.VISIBLE
                            }
                        }
                        is NetworkState.Error -> {
                            if(searchViewVisibility){
                                progressBarFsResult.visibility = View.GONE
                                recyclerViewFsResult.visibility = View.GONE
                                textFsResultError.apply {
                                    text = state.msg
                                    visibility = View.VISIBLE
                                }
                            }else{
                                progressBarFsBookmark.visibility = View.GONE
                                recyclerViewFsBookmark.visibility = View.GONE
                                textFsBookmarkError.apply {
                                    text = state.msg
                                    visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initToolbar(binding: FragmentFormSearchBinding){
        binding.toolbarFormSearch.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun settingBookmarkRV(binding: FragmentFormSearchBinding, schedulePosition: Int ){
        val flexboxLayoutManager = FlexboxLayoutManager(requireActivity()).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            alignItems = AlignItems.FLEX_START
            justifyContent = JustifyContent.FLEX_START
        }

        viewModel.bookmarkedPlaces.observe(viewLifecycleOwner){
            if(it.isNotEmpty()){
                binding.recyclerViewFsBookmark.apply {
                    layoutManager = flexboxLayoutManager
                    adapter = FormBookmarkedPlacesAdapter(it){ selectedPlacePosition ->
                        addNewPlace(schedulePosition, selectedPlacePosition, true)
                    }
                }
            }else{
                // 북마크한 여행지가 없다면
                binding.apply {
                    recyclerViewFsBookmark.visibility = View.INVISIBLE
                    textFsBookmarkEmpty.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun addNewPlace(
        schedulePosition: Int,
        selectedPlacePosition: Int,
        isBookmarkedPlace: Boolean
    ) {
        val isDuplicate = viewModel.isPlaceAlreadyAdded(
            schedulePosition,
            selectedPlacePosition,
            isBookmarkedPlace
        )
        if (isDuplicate) {
            requireView().showSnackbar("이 여행지는 이미 일정에 추가되어 있습니다")
        } else {
            findNavController().popBackStack()
        }
    }

    private fun settingSearchResultRV(binding: FragmentFormSearchBinding){
        binding.recyclerViewFsResult.apply {
            adapter = searchResultAdapter
            addOnScrollEndListener{
                with(viewModel){
                    if(!isLastPage()){
                        getPlaceSearchResult(isNewRequest = false)
                    }
                }
            }
        }

        viewModel.searchResultsWithNum.observe(viewLifecycleOwner) {
            // ListAdapter 사용 시, submitList 메서드를 호출하여 데이터를 전달해준다.
            searchResultAdapter.submitList(it)
            if (it.size <= 1) {
                binding.textFsResultEmpty.visibility = View.VISIBLE
            }
        }
    }

    private fun settingPlaceSearchView(binding: FragmentFormSearchBinding){
        binding.searchViewFsResult.apply {
            editText.setOnEditorActionListener { textView, actionId, event ->
                if(event!=null && event.action == KeyEvent.ACTION_DOWN){
                    val word = editText.text.toString()
                    if(word.isEmpty()){
                        this.showSnackbar("검색어를 입력해주세요")
                    }else{
                        binding.recyclerViewFsResult.visibility = View.VISIBLE
                        binding.textFsResultEmpty.visibility = View.GONE
                        viewModel.setKeyword(word)
                        viewModel.getPlaceSearchResult(isNewRequest = true)
                    }
                }
                false
            }
        }
    }

    private fun navigateToPlaceDetail(placeId: Long){
        val intent = Intent(requireActivity(), DetailActivity::class.java)
        intent.putExtra("detailPlaceId", placeId)
        startActivity(intent)
    }
}