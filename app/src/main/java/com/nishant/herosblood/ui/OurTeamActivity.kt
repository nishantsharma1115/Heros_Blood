package com.nishant.herosblood.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nishant.herosblood.databinding.ActivityOurTeamBinding

class OurTeamActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOurTeamBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOurTeamBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}