package kr.tekit.lion.presentation.main.bottomsheet

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.PlaceBottomSheetBinding
import kr.tekit.lion.presentation.main.adapter.DisabilityRVAdapter
import kr.tekit.lion.presentation.main.model.MapPlaceModel

class PlaceBottomSheet(
    private val place: MapPlaceModel,
    private val onClick: (String) -> Unit
): BottomSheetDialogFragment(R.layout.place_bottom_sheet) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = PlaceBottomSheetBinding.bind(view)

        val adapter = DisabilityRVAdapter(place.disability)
        with(binding){

            root.setOnClickListener {
                onClick(place.placeId)
            }

            Glide.with(binding.thumbnailImg.context)
                .load(place.placeImg)
                .error(R.drawable.empty_view)
                .placeholder(R.drawable.empty_view)
                .into(binding.thumbnailImg)

            textPlaceName.text = place.placeName
            textAddr.text = place.placeAddr

            disabilityRv.adapter = adapter
            disabilityRv.layoutManager = LinearLayoutManager(
                    root.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
            )
        }
    }
}