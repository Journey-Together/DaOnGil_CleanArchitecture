package kr.techit.lion.presentation.myreview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import kr.techit.lion.domain.model.MyPlaceReview
import kr.techit.lion.domain.model.MyPlaceReviewInfo
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ItemMyReviewBinding
import kr.techit.lion.presentation.databinding.ItemMyReviewHeaderBinding

class MyReviewRVAdapter(
    private val myReviewList: MyPlaceReview,
    private val myReviewListInfo: List<MyPlaceReviewInfo>,
    private val onMoveReviewListClick: (Long) -> Unit,
    private val onModifyClick: (MyPlaceReviewInfo) -> Unit,
    private val onDeleteClick: (Long) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            ViewType.HEADER.ordinal
        } else {
            ViewType.ITEM.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (ViewType.entries[viewType]) {
            ViewType.HEADER -> {
                val binding: ItemMyReviewHeaderBinding = ItemMyReviewHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                MyReviewHeaderViewHolder(binding)
            }
            ViewType.ITEM -> {
                val binding: ItemMyReviewBinding = ItemMyReviewBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                MyReviewViewHolder(binding, onMoveReviewListClick, onModifyClick, onDeleteClick)
            }
        }
    }

    override fun getItemCount(): Int = myReviewListInfo.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyReviewHeaderViewHolder) {
            val headerText = holder.itemView.context.getString(R.string.text_my_review_header, myReviewList.reviewNum)
            holder.bind(headerText)
        } else if (holder is MyReviewViewHolder) {
            holder.bind(myReviewListInfo[position - 1])
        }
    }

    class MyReviewViewHolder(
        private val binding: ItemMyReviewBinding,
        private val onMoveReviewListClick: (Long) -> Unit,
        private val onModifyClick: (MyPlaceReviewInfo) -> Unit,
        private val onDeleteClick: (Long) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(myPlaceReview: MyPlaceReviewInfo) {
            if (myPlaceReview.isReport == true) {
                binding.reviewLayout.visibility = View.GONE
                binding.reportLayout.visibility = View.VISIBLE

                binding.textViewMyReviewLocationName.text = myPlaceReview.name

                binding.layoutLocationName.setOnClickListener {
                    onMoveReviewListClick(myPlaceReview.placeId)
                }

                val locationNameDescription = binding.root.context.getString(R.string.text_my_review_location_name, myPlaceReview.name)
                binding.textViewMyReviewLocationName.contentDescription = locationNameDescription
            } else {
                binding.reviewLayout.visibility = View.VISIBLE
                binding.reportLayout.visibility = View.GONE

                binding.textViewMyReviewLocationName.text = myPlaceReview.name
                binding.ratingbarItemMyReview.rating = myPlaceReview.grade
                binding.textViewMyReviewDate.text = myPlaceReview.date.toString()
                binding.textViewMyReviewContent.text = myPlaceReview.content

                binding.layoutLocationName.setOnClickListener {
                    onMoveReviewListClick(myPlaceReview.placeId)
                }

                binding.myReviewModifyBtn.setOnClickListener {
                    onModifyClick(myPlaceReview)
                }

                binding.myReviewDeleteBtn.setOnClickListener {
                    onDeleteClick(myPlaceReview.reviewId)
                }

                val myReviewImageRVAdapter = MyReviewImageRVAdapter(myPlaceReview.images)
                binding.recyclerViewMyReivew.adapter = myReviewImageRVAdapter

                binding.textViewMyReviewDate.post {
                    val dateViewRight = binding.textViewMyReviewDate.right
                    val modifyButtonLeft = binding.myReviewModifyBtn.left

                    if (dateViewRight > modifyButtonLeft) {
                        val constraintSet = ConstraintSet()
                        constraintSet.clone(binding.reviewLayout)

                        constraintSet.connect(binding.textViewMyReviewDate.id, ConstraintSet.TOP, binding.ratingbarItemMyReview.id, ConstraintSet.BOTTOM, 8)
                        constraintSet.connect(binding.textViewMyReviewDate.id, ConstraintSet.START, binding.ratingbarItemMyReview.id, ConstraintSet.START, 0)
                        constraintSet.connect(binding.textViewMyReviewContent.id, ConstraintSet.TOP, binding.textViewMyReviewDate.id, ConstraintSet.BOTTOM, 16)

                        constraintSet.applyTo(binding.reviewLayout)
                    }
                }

                val locationNameDescription = binding.root.context.getString(R.string.text_my_review_location_name, myPlaceReview.name)
                binding.textViewMyReviewLocationName.contentDescription = locationNameDescription

                val ratingDescription = binding.root.context.getString(R.string.text_my_review_rating, myPlaceReview.grade)
                binding.ratingbarItemMyReview.contentDescription = ratingDescription
            }
        }
    }

    class MyReviewHeaderViewHolder(
        private val binding: ItemMyReviewHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(headerText: String) {
            binding.textViewMyReivewHeader.text = headerText
        }
    }
}

enum class ViewType {
    HEADER,
    ITEM
}