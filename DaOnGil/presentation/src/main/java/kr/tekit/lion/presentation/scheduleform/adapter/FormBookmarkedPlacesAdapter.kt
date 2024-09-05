package kr.tekit.lion.presentation.scheduleform.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.tekit.lion.domain.model.BookmarkedPlace
import kr.tekit.lion.presentation.databinding.ItemFormBookmarkedPlacesBinding

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
            binding.buttonBookmarkedPlace.text = place.bookmarkedPlaceName
        }
    }
}