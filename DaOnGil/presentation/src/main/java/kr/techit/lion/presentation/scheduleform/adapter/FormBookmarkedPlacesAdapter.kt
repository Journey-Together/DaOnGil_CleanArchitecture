package kr.techit.lion.presentation.scheduleform.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.techit.lion.domain.model.BookmarkedPlace
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ItemFormBookmarkedPlacesBinding
import kr.techit.lion.presentation.ext.setAccessibilityText

class FormBookmarkedPlacesAdapter(
    private val bookmarkedPlaces: List<BookmarkedPlace>,
    private val onPlaceSelectedListener: (selectedBookmarkPosition: Int) -> Unit
) : RecyclerView.Adapter<FormBookmarkedPlacesAdapter.FormBookmarkedPlacesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : FormBookmarkedPlacesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FormBookmarkedPlacesViewHolder(
            ItemFormBookmarkedPlacesBinding.inflate(inflater, parent, false),
            onPlaceSelectedListener
        )
    }

    override fun onBindViewHolder(holder: FormBookmarkedPlacesViewHolder, position: Int) {
        holder.bind(bookmarkedPlaces[position])
    }

    override fun getItemCount(): Int {
        return bookmarkedPlaces.size
    }

    class FormBookmarkedPlacesViewHolder(
        private val binding: ItemFormBookmarkedPlacesBinding,
        private val onPlaceSelectedListener: (selectedBookmarkPosition: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.buttonBookmarkedPlace.setOnClickListener {
                onPlaceSelectedListener(absoluteAdapterPosition)
            }
        }

        fun bind(place: BookmarkedPlace) {
            val bookmarkedPlace = place.bookmarkedPlaceName

            binding.buttonBookmarkedPlace.apply {
                text = bookmarkedPlace
                setAccessibilityText(
                    itemView.context.getString(
                        R.string.accessibility_text_add_bookmarked_place,
                        bookmarkedPlace
                    )
                )
            }
        }
    }
}