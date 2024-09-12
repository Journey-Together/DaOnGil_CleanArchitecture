package kr.tekit.lion.presentation.scheduleform.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.tekit.lion.domain.model.scheduleform.FormPlace
import kr.tekit.lion.presentation.databinding.ItemFormEmptyBinding
import kr.tekit.lion.presentation.databinding.ItemFormPlaceBinding
import kr.tekit.lion.presentation.ext.setImageSmall

class FormConfirmPlaceAdapter(private val places: List<FormPlace>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_PLACE = 0
    private val VIEW_TYPE_EMPTY = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            VIEW_TYPE_PLACE -> {
                return FormConfirmPlaceViewHolder(
                    ItemFormPlaceBinding.inflate(
                        inflater, parent, false
                    )
                )
            }
            // VIEW_TYPE_EMPTY
            else -> {
                return FormConfirmEmptyViewHolder(
                    ItemFormEmptyBinding.inflate(
                        inflater, parent, false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FormConfirmPlaceViewHolder -> {
                places[position]?.let {
                    holder.bind(it)
                }
            }

            is FormConfirmEmptyViewHolder -> {}
        }
    }

    override fun getItemCount(): Int {
        return if (places.isEmpty()) 1 else places.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (places.isNotEmpty()) VIEW_TYPE_PLACE else VIEW_TYPE_EMPTY
    }

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

    class FormConfirmEmptyViewHolder(private val binding: ItemFormEmptyBinding) :
        RecyclerView.ViewHolder(binding.root)

}