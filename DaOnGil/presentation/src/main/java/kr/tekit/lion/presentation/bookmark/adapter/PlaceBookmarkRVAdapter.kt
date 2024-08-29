package kr.tekit.lion.presentation.bookmark.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.tekit.lion.domain.model.PlaceBookmark
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ItemPlaceBookmarkBinding

class PlaceBookmarkRVAdapter(
    private val placeBookmarkList: List<PlaceBookmark>,
    private val itemClickListener: (Int) -> Unit,
    private val onBookmarkClick: (Long) -> Unit
) : RecyclerView.Adapter<PlaceBookmarkRVAdapter.PlaceBookmarkViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceBookmarkViewHolder {
        val binding = ItemPlaceBookmarkBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return PlaceBookmarkViewHolder(binding, itemClickListener, onBookmarkClick)
    }

    override fun getItemCount(): Int = placeBookmarkList.size

    override fun onBindViewHolder(holder: PlaceBookmarkViewHolder, position: Int) {
        holder.bind(placeBookmarkList[position])
    }

    class PlaceBookmarkViewHolder(
        val binding: ItemPlaceBookmarkBinding,
        private val itemClickListener: (Int) -> Unit,
        private val onBookmarkClick: (Long) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                itemClickListener.invoke(bindingAdapterPosition)
            }
        }

        fun bind(placeBookmark: PlaceBookmark) {
            binding.textViewLocationBookmark.text = placeBookmark.address
            binding.textViewLocationBookmarkName.text = placeBookmark.name

            Glide.with(binding.imageViewLocationBookmark.context)
                .load(placeBookmark.image)
                .placeholder(R.drawable.empty_view_long)
                .error(R.drawable.empty_view_long)
                .into(binding.imageViewLocationBookmark)

            binding.locationBookmarkBtn.setOnClickListener {
                onBookmarkClick(placeBookmark.placeId)
            }

            val disabilityList = placeBookmark.disability

            val bookmarkDisabilityRVAdapter = BookmarkDisabilityRvAdapter(disabilityList)
            binding.recyclerViewLocationBookmark.adapter = bookmarkDisabilityRVAdapter
        }
    }
}