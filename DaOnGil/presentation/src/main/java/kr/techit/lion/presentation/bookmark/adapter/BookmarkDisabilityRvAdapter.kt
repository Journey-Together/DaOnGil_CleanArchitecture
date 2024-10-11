package kr.techit.lion.presentation.bookmark.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ItemDisabilityTypeBinding

class BookmarkDisabilityRvAdapter(private val typeList : List<String>)
    : RecyclerView.Adapter<BookmarkDisabilityRvAdapter.BookmarkDisabilityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkDisabilityViewHolder {
        val binding = ItemDisabilityTypeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return BookmarkDisabilityViewHolder(binding)
    }

    override fun getItemCount(): Int = typeList.size

    override fun onBindViewHolder(holder: BookmarkDisabilityViewHolder, position: Int) {
        holder.bind(typeList[position])
    }

    class BookmarkDisabilityViewHolder(private val binding: ItemDisabilityTypeBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item : String) {
            val typeId = when(item) {
                "1" -> R.drawable.physical_disability_radius_icon
                "2" -> R.drawable.visual_impairment_radius_icon
                "3" -> R.drawable.hearing_impairment_radius_icon
                "4" -> R.drawable.infant_familly_radius_icon
                else -> R.drawable.elderly_people_radius_icon
            }
            binding.itemDisabilityTypeIv.setImageResource(typeId)
        }
    }

    fun getDisabilityDescriptions(context: Context): List<String> {
        return typeList.map { type ->
            when (type) {
                "1" -> context.getString(R.string.text_physical_disability)
                "2" -> context.getString(R.string.text_visual_impairment)
                "3" -> context.getString(R.string.text_hearing_impairment)
                "4" -> context.getString(R.string.text_infant_family)
                else -> context.getString(R.string.text_elderly_person)
            }
        }
    }
}