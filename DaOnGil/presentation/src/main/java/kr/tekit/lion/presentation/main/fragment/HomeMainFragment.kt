package kr.tekit.lion.presentation.main.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.domain.model.AppTheme
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentHomeMainBinding
import kr.tekit.lion.presentation.ext.repeatOnViewStarted
import kr.tekit.lion.presentation.main.vm.home.HomeViewModel

@AndroidEntryPoint
class HomeMainFragment : Fragment(R.layout.fragment_home_main) {
    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHomeMainBinding.bind(view)
        repeatOnViewStarted {
            viewModel.appTheme.collect{
                when(it){
                    AppTheme.LIGHT ->
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    AppTheme.DARK ->
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    AppTheme.SYSTEM ->
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
        }
        binding.button.setOnClickListener {
            viewModel.onClickThemeToggleButton()
            startActivity(Intent.makeRestartActivityTask(activity?.intent?.component))
        }
    }
}