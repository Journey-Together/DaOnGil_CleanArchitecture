package kr.tekit.lion.presentation.main.adapter.viewholder

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ItemListSearchCategoryBinding
import kr.tekit.lion.presentation.ext.isTallBackEnabled
import kr.tekit.lion.presentation.ext.setAccessibilityText
import kr.tekit.lion.presentation.ext.setClickEvent
import kr.tekit.lion.presentation.main.model.CategoryModel
import kr.tekit.lion.presentation.main.model.ElderlyPeople
import kr.tekit.lion.presentation.main.model.HearingImpairment
import kr.tekit.lion.presentation.main.model.InfantFamily
import kr.tekit.lion.presentation.main.model.PhysicalDisability
import kr.tekit.lion.presentation.main.model.VisualImpairment

class CategoryViewHolder(
    private val binding: ItemListSearchCategoryBinding,
    private val uiScope: CoroutineScope,
    private val onClickPhysicalDisability: (PhysicalDisability) -> Unit,
    private val onClickVisualImpairment: (VisualImpairment) -> Unit,
    private val onClickHearingDisability: (HearingImpairment) -> Unit,
    private val onClickInfantFamily: (InfantFamily) -> Unit,
    private val onClickElderlyPeople: (ElderlyPeople) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        with(binding) {
            imgPhysicalDisability.setClickEvent(uiScope) {
                onClickPhysicalDisability(PhysicalDisability)
            }
            imgVisualDisability.setClickEvent(uiScope) {
                onClickVisualImpairment(VisualImpairment)
            }
            imgHearingDisability.setClickEvent(uiScope) {
                onClickHearingDisability(HearingImpairment)
            }
            imgInfantFamily.setClickEvent(uiScope) {
                onClickInfantFamily(InfantFamily)
            }
            imgElderlyPerson.setClickEvent(uiScope) {
                onClickElderlyPeople(ElderlyPeople)
            }
        }
    }

    fun bind(item: CategoryModel) {
        modifyPhysicalDisabilityUI(item)
        modifyVisualImpairmentUI(item)
        modifyHearingImpairmentUI(item)
        modifyInfantFamilyUI(item)
        modifyElderlyPeopleUI(item)
    }

    private fun modifyDisabilityUI(
        count: Int,
        textView: TextView,
        imageView: ImageView,
        unselectedIcon: Int,
        selectedIcon: Int,
        textResId: Int
    ) {
        val context = binding.root.context
        when (count) {
            0, -1 -> {
                val currentText = context.getString(textResId)
                textView.text = currentText
                textView.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.search_view_category_name
                    )
                )
                imageView.setImageDrawable(ContextCompat.getDrawable(context, unselectedIcon))
                if (context.isTallBackEnabled()) imageView.setAccessibilityText(currentText)
            }

            else -> {
                val currentText = "${context.getString(textResId)} $count"
                textView.text = currentText
                textView.setTextColor(ContextCompat.getColor(context, R.color.search_view_main))
                imageView.setImageDrawable(ContextCompat.getDrawable(context, selectedIcon))
                if (context.isTallBackEnabled()) {
                    imageView.setAccessibilityText(currentText + "개 옵션이 선택되었습니다")
                }
            }
        }
    }

    private fun modifyPhysicalDisabilityUI(item: CategoryModel) {
        val count = item.optionState[PhysicalDisability] ?: 0
        with(binding) {
            modifyDisabilityUI(
                count,
                textPhysicalDisability,
                imgPhysicalDisability,
                R.drawable.sv_physical_disability_unselected_icon,
                R.drawable.sv_physical_disability_selected_icon,
                R.string.text_physical_disability
            )
        }
    }

    private fun modifyVisualImpairmentUI(item: CategoryModel) {
        val count = item.optionState[VisualImpairment] ?: 0
        with(binding) {
            modifyDisabilityUI(
                count,
                textVisualDisability,
                imgVisualDisability,
                R.drawable.sv_visual_impairment_unselect_icon,
                R.drawable.sv_visual_impairment_select_icon,
                R.string.text_visual_impairment
            )
        }
    }

    private fun modifyHearingImpairmentUI(item: CategoryModel) {
        val count = item.optionState[HearingImpairment] ?: 0
        with(binding) {
            modifyDisabilityUI(
                count,
                textHearingDisability,
                imgHearingDisability,
                R.drawable.sv_hearing_impaired_unselected_icon,
                R.drawable.sv_hearing_impaired_selected_icon,
                R.string.text_hearing_impairment
            )
        }
    }

    private fun modifyInfantFamilyUI(item: CategoryModel) {
        val count = item.optionState[InfantFamily] ?: 0
        with(binding) {
            modifyDisabilityUI(
                count,
                textInfantFamily,
                imgInfantFamily,
                R.drawable.sv_child_unselected_icon,
                R.drawable.sv_child_selected_icon,
                R.string.text_infant_family
            )
        }
    }

    private fun modifyElderlyPeopleUI(item: CategoryModel) {
        val count = item.optionState[ElderlyPeople] ?: 0
        with(binding) {
            modifyDisabilityUI(
                count,
                textElderlyPerson,
                imgElderlyPerson,
                R.drawable.sv_elderly_unselected_icon,
                R.drawable.sv_elderly_selected_icon,
                R.string.text_elderly_person
            )
        }
    }
}