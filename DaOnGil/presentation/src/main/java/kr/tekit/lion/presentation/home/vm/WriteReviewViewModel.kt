package kr.tekit.lion.presentation.home.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.model.placereview.NewReviewData
import kr.tekit.lion.domain.model.placereview.NewReviewImages
import kr.tekit.lion.domain.repository.PlaceRepository
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import org.json.JSONObject
import java.time.LocalDate
import kr.tekit.lion.domain.exception.onError
import kr.tekit.lion.domain.exception.onSuccess
import kr.tekit.lion.presentation.home.model.NewReviewImgs
import javax.inject.Inject

@HiltViewModel
class WriteReviewViewModel @Inject constructor(
    private val writePlaceReviewRepository: PlaceRepository
) : ViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    private val _placeVisitDate = MutableLiveData<LocalDate>()
    val placeVisitDate: LiveData<LocalDate> = _placeVisitDate

    private val _newReviewData = MutableLiveData<NewReviewData>()
    val newReviewData : LiveData<NewReviewData> = _newReviewData

    private val _newReviewImages = MutableLiveData<NewReviewImgs>()
    val newReviewImages : LiveData<NewReviewImgs> = _newReviewImages

    fun setPlaceVisitDate(startDate: LocalDate) {
        _placeVisitDate.value = startDate
    }

    fun writePlaceReviewData(placeId: Long, date: LocalDate, grade: Float, content: String, callback: (Boolean, Boolean, Int) -> Unit) {
        var requestFlag = false
        var code = 0

        _newReviewData.value = newReviewData.value?.copy(
            date = date,
            grade = grade,
            content = content
        )
        viewModelScope.launch{
            val success = try {
                newReviewImages.value?.let {
                    writePlaceReviewRepository.writePlaceReviewData(placeId, NewReviewData(date, grade, content), it.toDomainModel())
                        .onSuccess {
                            Log.e("writePlaceReviewData image", "images : ${newReviewImages.value}")
                            requestFlag = true
                            val json = JSONObject(it.toString())
                            val successCode = json.getInt("code")
                            Log.d("writePlaceReviewData", json.toString())

                            code = successCode
                        }.onError {
                            networkErrorDelegate.handleNetworkError(it)
                        }
                }
                true
            } catch (e: Exception) {
                Log.d("writePlaceReviewData", "Error : ${e.message}")
                false
            }
            callback(success, requestFlag, code)
        }
    }

    fun setReviewImages(image: String) {
        val currentImages = _newReviewImages.value?.images?.toMutableList() ?: mutableListOf()
        currentImages.add(image)
        _newReviewImages.value = NewReviewImgs(currentImages)
    }


    fun deleteImage(position: Int) {
        val currentImages = _newReviewImages.value?.images?.toMutableList() ?: mutableListOf()
        if (position in currentImages.indices) {
            currentImages.removeAt(position)
            _newReviewImages.value = NewReviewImgs(currentImages)
        }
    }
}