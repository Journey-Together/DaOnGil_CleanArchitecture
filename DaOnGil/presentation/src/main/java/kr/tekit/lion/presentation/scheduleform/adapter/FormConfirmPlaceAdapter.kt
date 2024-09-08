package kr.tekit.lion.presentation.scheduleform.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.tekit.lion.domain.model.scheduleform.FormPlace
import kr.tekit.lion.presentation.databinding.ItemFormPlaceBinding
import kr.tekit.lion.presentation.ext.setImageSmall

class FormConfirmPlaceAdapter(private val places: List<FormPlace>) :
    RecyclerView.Adapter<FormConfirmPlaceAdapter.FormConfirmPlaceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormConfirmPlaceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FormConfirmPlaceViewHolder(
            ItemFormPlaceBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FormConfirmPlaceViewHolder, position: Int) {
        holder.bind(places[position])
    }

    override fun getItemCount(): Int = places.size

    class FormConfirmPlaceViewHolder(private val binding: ItemFormPlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(place: FormPlace) {
            binding.apply {
                buttonFormPlaceRemove.visibility = View.GONE

                textFormPlaceCategory.text = place.placeCategory
                textFormPlaceName.text = place.placeName

                place.placeImage?.let {
                    imageFormPlaceThumbnail.context.setImageSmall(
                        imageFormPlaceThumbnail, it
                    )
                }
            }
        }
    }
}