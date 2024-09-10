package kr.tekit.lion.presentation.scheduleform.fragment

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.View
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
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentFormSearchBinding
import kr.tekit.lion.presentation.delegate.NetworkState
import kr.tekit.lion.presentation.ext.addOnScrollEndListener
import kr.tekit.lion.presentation.ext.showSnackbar
import kr.tekit.lion.presentation.home.DetailActivity
import kr.tekit.lion.presentation.scheduleform.adapter.FormBookmarkedPlacesAdapter
import kr.tekit.lion.presentation.scheduleform.adapter.FormSearchResultAdapter
import kr.tekit.lion.presentation.scheduleform.vm.ScheduleFormViewModel

@AndroidEntryPoint
class FormSearchFragment : Fragment(R.layout.fragment_form_search) {
    private val args: FormSearchFragmentArgs by navArgs()
    private val viewModel: ScheduleFormViewModel by activityViewModels()

    private val searchResultAdapter by lazy {
        FormSearchResultAdapter(
            onPlaceSelectedListener = { selectedPlacePosition ->
                addNewPlaceToList(args.schedulePosition, selectedPlacePosition, false)
            },
            onItemClickListener = { selectedPlacePosition ->
                val placeId = viewModel.getPlaceId(selectedPlacePosition)
                if (placeId != -1L) {
                    navigateToPlaceDetail(placeId)
                }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 추가해야하는 일정의 position
        val schedulePosition = args.schedulePosition

        val binding = FragmentFormSearchBinding.bind(view)

        initProgressBar(binding)
        initBookmarkList()
        initToolbar(binding)

        settingBookmarkedRV(binding, schedulePosition)
        settingSearchResultRV(binding)
        settingPlaceSearchView(binding)

    }

    private fun initBookmarkList(){
        viewModel.initBookmarkList()
    }

    private fun initProgressBar(binding: FragmentFormSearchBinding) {
        with(binding) {
            lifecycleScope.launch {
                viewModel.networkState.collectLatest { state ->

                    when(state){
                        is NetworkState.Loading -> {
                            // SeachView가 화면에 표시되고 있는 지 확인
                            val searchViewVisibility = searchViewFsResult.isShowing

                            if(searchViewVisibility){
                                textFsResultError.visibility = View.GONE
                                progressBarFsResult.visibility = View.VISIBLE
                            }else{
                                progressBarFsBookmark.visibility = View.VISIBLE
                            }
                        }
                        is NetworkState.Success -> {
                            val searchViewVisibility = searchViewFsResult.isShowing
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
                            val searchViewVisibility = searchViewFsResult.isShowing
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

    private fun initToolbar(binding: FragmentFormSearchBinding) {
        binding.toolbarFormSearch.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun settingSearchResultRV(binding: FragmentFormSearchBinding) {
        binding.recyclerViewFsResult.apply {
            adapter = searchResultAdapter
            addOnScrollEndListener {
                with(viewModel) {
                    if (!isLastPage()) {
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

    private fun addNewPlaceToList(
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

    private fun settingPlaceSearchView(binding: FragmentFormSearchBinding) {
        binding.searchViewFsResult.apply {
            editText.setOnEditorActionListener { textView, actionId, event ->
                if (event != null && event.action == KeyEvent.ACTION_DOWN) {
                    val word = editText.text.toString()
                    if (word.isEmpty()) {
                        this.showSnackbar("검색어를 입력해주세요")
                    } else {
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

    private fun settingBookmarkedRV(binding: FragmentFormSearchBinding, schedulePosition: Int) {
        val flexboxLayoutManager = FlexboxLayoutManager(requireActivity()).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            alignItems = AlignItems.FLEX_START
            justifyContent = JustifyContent.FLEX_START
        }

        viewModel.bookmarkedPlaces.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.recyclerViewFsBookmark.apply {
                    layoutManager = flexboxLayoutManager
                    adapter = FormBookmarkedPlacesAdapter(it) { selectedPlacePosition ->
                        addNewPlaceToList(schedulePosition, selectedPlacePosition, true)
                    }
                }
            } else {
                // 북마크한 여행지가 없다면
                binding.apply {
                    recyclerViewFsBookmark.visibility = View.INVISIBLE
                    textFsBookmarkEmpty.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun navigateToPlaceDetail(placeId: Long){
        val intent = Intent(requireActivity(), DetailActivity::class.java)
        intent.putExtra("detailPlaceId", placeId)
        startActivity(intent)
    }
}