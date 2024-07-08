package kr.tekit.lion.presentation.main

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.CategoryBottomSheetLayoutBinding
import kr.tekit.lion.presentation.main.model.DisabilityType
import kr.tekit.lion.presentation.main.model.Options

class CategoryBottomSheet(
    private val selectedOption: List<Int>,
    private val selectedCategory: DisabilityType,
    private val itemClick: (List<Int>, List<Long>) -> Unit
) : BottomSheetDialogFragment(R.layout.category_bottom_sheet_layout) {
    private val selectedOptions = ArrayList<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = CategoryBottomSheetLayoutBinding.bind(view)

        initChips(binding)
        setOptions(binding)
    }

    private fun initChips(binding: CategoryBottomSheetLayoutBinding) {
        if (selectedOption.isNotEmpty()) {
            selectedOption.map {
                when (selectedCategory) {
                    DisabilityType.PhysicalDisability -> {
                        val chip = binding.chipGroupPhysicalDisability.findViewById<Chip>(it)
                        chip.isChecked = true
                    }

                    DisabilityType.HearingImpairment -> {
                        val chip = binding.chipGroupHearingImpairment.findViewById<Chip>(it)
                        chip.isChecked = true
                    }

                    DisabilityType.VisualImpairment -> {
                        val chip = binding.chipGroupVisualImpairment.findViewById<Chip>(it)
                        chip.isChecked = true
                    }

                    DisabilityType.InfantFamily -> {
                        val chip = binding.chipGroupInfantFamily.findViewById<Chip>(it)
                        chip.isChecked = true
                    }

                    DisabilityType.ElderlyPeople -> {
                        val chip = binding.chipGroupElderlyPerson.findViewById<Chip>(it)
                        chip.isChecked = true
                    }
                }
            }
        }
    }


    private fun setOptions(binding: CategoryBottomSheetLayoutBinding) {
        with(binding) {
            when (selectedCategory) {
                DisabilityType.PhysicalDisability -> {
                    chipGroupPhysicalDisability.visibility = View.VISIBLE
                    chipGroupVisualImpairment.visibility = View.GONE
                    chipGroupHearingImpairment.visibility = View.GONE
                    chipGroupInfantFamily.visibility = View.GONE
                    chipGroupElderlyPerson.visibility = View.GONE
                }

                DisabilityType.HearingImpairment -> {
                    chipGroupHearingImpairment.visibility = View.VISIBLE
                    chipGroupPhysicalDisability.visibility = View.GONE
                    chipGroupVisualImpairment.visibility = View.GONE
                    chipGroupInfantFamily.visibility = View.GONE
                    chipGroupElderlyPerson.visibility = View.GONE

                }

                DisabilityType.VisualImpairment -> {
                    chipGroupVisualImpairment.visibility = View.VISIBLE
                    chipGroupPhysicalDisability.visibility = View.GONE
                    chipGroupHearingImpairment.visibility = View.GONE
                    chipGroupInfantFamily.visibility = View.GONE
                    chipGroupElderlyPerson.visibility = View.GONE

                }

                DisabilityType.InfantFamily -> {
                    chipGroupInfantFamily.visibility = View.VISIBLE
                    chipGroupPhysicalDisability.visibility = View.GONE
                    chipGroupVisualImpairment.visibility = View.GONE
                    chipGroupHearingImpairment.visibility = View.GONE
                    chipGroupElderlyPerson.visibility = View.GONE

                }

                DisabilityType.ElderlyPeople -> {
                    chipGroupElderlyPerson.visibility = View.VISIBLE
                    chipGroupPhysicalDisability.visibility = View.GONE
                    chipGroupVisualImpairment.visibility = View.GONE
                    chipGroupHearingImpairment.visibility = View.GONE
                    chipGroupInfantFamily.visibility = View.GONE
                }
            }

            allChip.setOnClickListener {
                selectedOptions.clear()

                when (selectedCategory) {
                    DisabilityType.PhysicalDisability -> {
                        if (allChip.isChecked) allChip.isChecked = false
                        chipGroupPhysicalDisability.check(R.id.wheelchair_chip)
                        chipGroupPhysicalDisability.check(R.id.parking_chip)
                        chipGroupPhysicalDisability.check(R.id.elevator_chip)
                        chipGroupPhysicalDisability.check(R.id.restroom_chip)
                    }

                    DisabilityType.VisualImpairment -> {
                        if (allChip.isChecked) allChip.isChecked = false
                        chipGroupVisualImpairment.check(R.id.braileblock_chip)
                        chipGroupVisualImpairment.check(R.id.help_dog_chip)
                        chipGroupVisualImpairment.check(R.id.guide_chip)
                        chipGroupVisualImpairment.check(R.id.audio_guide_chip)
                    }

                    DisabilityType.HearingImpairment -> {
                        if (allChip.isChecked) allChip.isChecked = false
                        chipGroupHearingImpairment.check(R.id.sign_guide_chip)
                        chipGroupHearingImpairment.check(R.id.video_guide_chip)
                    }

                    DisabilityType.InfantFamily -> {
                        if (allChip.isChecked) allChip.isChecked = false
                        chipGroupInfantFamily.check(R.id.stroller_chip)
                        chipGroupInfantFamily.check(R.id.lactation_room_chip)
                        chipGroupInfantFamily.check(R.id.baby_spare_chair_chip)
                    }

                    DisabilityType.ElderlyPeople -> {
                        if (allChip.isChecked) allChip.isChecked = false
                        chipGroupInfantFamily.check(R.id.lend_chip)
                    }
                }
            }

            btnReset.setOnClickListener {
                when (selectedCategory) {
                    DisabilityType.PhysicalDisability -> {
                        allChip.isChecked = false
                        chipGroupPhysicalDisability.clearCheck()
                    }

                    DisabilityType.HearingImpairment -> {
                        allChip.isChecked = false
                        chipGroupHearingImpairment.clearCheck()
                    }

                    DisabilityType.VisualImpairment -> {
                        allChip.isChecked = false
                        chipGroupVisualImpairment.clearCheck()
                    }

                    DisabilityType.InfantFamily -> {
                        allChip.isChecked = false
                        chipGroupInfantFamily.clearCheck()
                    }

                    DisabilityType.ElderlyPeople -> {
                        allChip.isChecked = false
                        chipGroupElderlyPerson.clearCheck()
                    }
                }
            }

            btnSubmit.setOnClickListener {
                val selectedChipIds = when (selectedCategory) {
                    is DisabilityType.PhysicalDisability -> {
                        chipGroupPhysicalDisability.checkedChipIds
                    }

                    is DisabilityType.HearingImpairment -> {
                        allChip.isChecked = false
                        chipGroupHearingImpairment.checkedChipIds
                    }

                    is DisabilityType.VisualImpairment -> {
                        allChip.isChecked = false
                        chipGroupVisualImpairment.checkedChipIds
                    }

                    is DisabilityType.InfantFamily -> {
                        allChip.isChecked = false
                        chipGroupInfantFamily.checkedChipIds
                    }

                    is DisabilityType.ElderlyPeople -> {
                        allChip.isChecked = false
                        chipGroupElderlyPerson.checkedChipIds
                    }
                }

                val selectedChipTexts = selectedChipIds.map { chipId ->
                    when(chipId){
                        R.id.parking_chip -> Options.Parking.code
                        R.id.wheelchair_chip -> Options.Wheelchair.code
                        R.id.restroom_chip -> Options.RestRoom.code
                        R.id.elevator_chip -> Options.Elevator.code
                        R.id.seat_chip -> Options.Seat.code
                        R.id.braileblock_chip -> Options.Braileblock.code
                        R.id.help_dog_chip -> Options.HelpDog.code
                        R.id.guide_chip -> Options.Guide.code
                        R.id.audio_guide_chip -> Options.AudioGuide.code
                        R.id.sign_guide_chip -> Options.SignGuide.code
                        R.id.video_guide_chip -> Options.VideoGuide.code
                        R.id.stroller_chip -> Options.Stroller.code
                        R.id.lactation_room_chip -> Options.LactationRoom.code
                        R.id.baby_spare_chair_chip -> Options.BabySpareChair.code
                        else -> Options.WheelchairLent.code
                    }
                }
                itemClick(selectedChipIds, selectedChipTexts)
                dismiss()
            }

        }
    }

    override fun getTheme(): Int {
        return R.style.category_bottom_sheet_dialog_theme
    }
}
