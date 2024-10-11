package kr.techit.lion.presentation.scheduleform.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.techit.lion.domain.model.scheduleform.FormPlace
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ItemFormPlaceBinding

class FormPlaceAdapter(
    private val places: List<FormPlace>,
    private val schedulePosition: Int,
    private val onItemClickListener: (schedulePosition: Int, placePosition: Int) -> Unit,
    private val onRemoveButtonClick: (schedulePosition: Int, placePosition: Int) -> Unit
) : RecyclerView.Adapter<FormPlaceAdapter.FormPlaceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormPlaceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FormPlaceViewHolder(
            ItemFormPlaceBinding.inflate(inflater, parent, false),
            schedulePosition,
            onItemClickListener,
            onRemoveButtonClick
        )
    }

    override fun onBindViewHolder(holder: FormPlaceViewHolder, position: Int) {
        holder.bind(places[position])
    }

    override fun getItemCount(): Int = places.size

    class FormPlaceViewHolder(
        private val binding: ItemFormPlaceBinding,
        private val schedulePosition: Int,
        private val onItemClickListener: (schedulePosition: Int, placePosition: Int) -> Unit,
        private val onRemoveItemClick: (schedulePosition: Int, placePosition: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.buttonFormPlaceRemove.setOnClickListener {
                onRemoveItemClick(schedulePosition, absoluteAdapterPosition)
            }
            binding.cardFormPlace.setOnClickListener {
                onItemClickListener(schedulePosition, absoluteAdapterPosition)
            }
        }

        fun bind(place: FormPlace) {
            binding.apply {
                textFormPlaceCategory.text = place.placeCategory
                textFormPlaceName.text = place.placeName

                place.placeImage?.let {
                    Glide.with(imageFormPlaceThumbnail.context)
                        .load(it)
                        .placeholder(R.drawable.empty_view)
                        .error(R.drawable.empty_view)
                        .into(imageFormPlaceThumbnail)
                }

                textFormPlaceName.accessibilityDelegate = object : View.AccessibilityDelegate() {
                    override fun onInitializeAccessibilityNodeInfo(
                        host: View,
                        info: AccessibilityNodeInfo
                    ) {
                        super.onInitializeAccessibilityNodeInfo(host, info)

                        info.text = itemView.context.getString(
                            R.string.accessibility_text_place,
                            place.placeCategory,
                            place.placeName
                        )
                    }
                }

                buttonFormPlaceRemove.contentDescription = itemView.context.getString(
                    R.string.accessibility_text_schedule_delete,
                    place.placeCategory,
                    place.placeName
                )
            }
        }
    }
}