package kr.techit.lion.presentation.setting

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kr.techit.lion.presentation.databinding.ActivityPolicyBinding

class PolicyActivity : AppCompatActivity() {
    private val binding: ActivityPolicyBinding by lazy {
        ActivityPolicyBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding){
            backButton.setOnClickListener {
                finish()
            }

            policyTitle1.setOnClickListener {
                if (policyCard1.visibility == View.VISIBLE){
                    policyCard1.visibility = View.GONE
                    policyBtn.animate().apply {
                        rotation(0f)
                        duration = 300
                        start()
                    }
                }else{
                    policyCard1.visibility = View.VISIBLE
                    policyBtn.animate().apply {
                        rotation(90f)
                        duration = 300
                        start()
                    }
                }
            }

            policyTitle2.setOnClickListener {
                if (policyCard2.visibility == View.VISIBLE){
                    policyCard2.visibility = View.GONE
                    policyBtn2.animate().apply {
                        rotation(0f)
                        duration = 300
                        start()
                    }
                }else{
                    policyCard2.visibility = View.VISIBLE
                    policyBtn2.animate().apply {
                        rotation(90f)
                        duration = 300
                        start()
                    }
                }
            }

            policyTitle3.setOnClickListener {
                if (policyCard3.visibility == View.VISIBLE){
                    policyCard3.visibility = View.GONE
                    policyBtn3.animate().apply {
                        rotation(0f)
                        duration = 300
                        start()
                    }
                }else{
                    policyCard3.visibility = View.VISIBLE
                    policyBtn3.animate().apply {
                        rotation(90f)
                        duration = 300
                        start()
                    }
                }
            }

            policyTitle4.setOnClickListener {
                if (policyCard4.visibility == View.VISIBLE){
                    policyCard4.visibility = View.GONE
                    policyBtn4.animate().apply {
                        rotation(0f)
                        duration = 300
                        start()
                    }
                }else{
                    policyCard4.visibility = View.VISIBLE
                    policyBtn4.animate().apply {
                        rotation(90f)
                        duration = 300
                        start()
                    }
                }
            }
        }
    }
}