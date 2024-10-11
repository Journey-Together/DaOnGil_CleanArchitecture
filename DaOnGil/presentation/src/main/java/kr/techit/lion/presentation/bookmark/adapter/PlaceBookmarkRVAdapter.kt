package kr.techit.lion.presentation.bookmark.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.techit.lion.domain.model.PlaceBookmark
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ItemPlaceBookmarkBinding
import kr.techit.lion.presentation.ext.isTallBackEnabled

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

            val bookmarkBtnDescription = binding.root.context.getString(R.string.text_update_place_bookmark, placeBookmark.name)
            binding.locationBookmarkBtn.contentDescription = bookmarkBtnDescription

            if (binding.root.context.isTallBackEnabled()) {
                ViewCompat.setAccessibilityDelegate(binding.root, object : AccessibilityDelegateCompat() {
                    override fun onInitializeAccessibilityNodeInfo(
                        host: View,
                        info: AccessibilityNodeInfoCompat
                    ) {
                        super.onInitializeAccessibilityNodeInfo(host, info)

                        val disabilityDescriptions = bookmarkDisabilityRVAdapter
                            .getDisabilityDescriptions(binding.root.context)
                            .joinToString(", ")

                        val combinedDescription = StringBuilder()
                            .append(binding.textViewLocationBookmarkName.text)
                            .append(", ")
                            .append(binding.textViewLocationBookmark.text)

                        if (disabilityDescriptions.isNotEmpty()) {
                            combinedDescription.append(", 관심 유형 정보: ").append(disabilityDescriptions)
                        }

                        info.text = combinedDescription.toString()
                    }
                })
            }
        }
    }
}