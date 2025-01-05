package kr.techit.lion.presentation.main.adapter.viewholder

import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ItemPlaceHighBinding
import kr.techit.lion.presentation.main.adapter.DisabilityRVAdapter
import kr.techit.lion.presentation.main.model.PlaceModel

class PlaceHighViewHolder(
    private val binding: ItemPlaceHighBinding,
    private val onSelectPlace: (Long) -> Unit
)
    : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: PlaceModel) {
        with(binding) {
            root.setOnClickListener {
                onSelectPlace(item.placeId)
            }

            tvName.text = item.placeName
            tvAddr.text = item.placeAddr
            val disabilityList = item.disability

            Glide.with(root)
                .load(item.placeImg)
                .placeholder(R.drawable.empty_view_small)
                .error(R.drawable.empty_view_small)
                .into(thumbnailImg)

            val disabilityRVAdapter = DisabilityRVAdapter(disabilityList)
            touristHighDisabilityRv.adapter = disabilityRVAdapter
            touristHighDisabilityRv.layoutManager = LinearLayoutManager(
                root.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )

            root.accessibilityDelegate = object : View.AccessibilityDelegate() {
                override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
                    super.onInitializeAccessibilityNodeInfo(host, info)

                    val disabilityText = disabilityList.map {
                        when (it) {
                            "1" -> root.context.getString(R.string.text_physical_disability)
                            "2" -> root.context.getString(R.string.text_visual_impairment)
                            "3" -> root.context.getString(R.string.text_hearing_impairment)
                            "4" -> root.context.getString(R.string.text_infant_family)
                            else -> root.context.getString(R.string.text_elderly_person)
                        }
                    }

                    val tallBackMsg = "${binding.tvName.text} $disabilityText 에 대한여행을 지원합니다." +
                            "주소는 ${binding.tvAddr.text}입니다."
                    info.hintText = null
                    info.text = tallBackMsg
                }
            }
        }
    }
}