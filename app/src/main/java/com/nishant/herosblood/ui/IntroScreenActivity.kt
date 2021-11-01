package com.nishant.herosblood.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.nishant.herosblood.R
import com.nishant.herosblood.databinding.ActivityIntroScreenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroScreenBinding
    private val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onStart() {
        super.onStart()
        if (user != null) {
            startActivity(Intent(this, UserDashboardActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.FullScreenTheme)
        binding = ActivityIntroScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}
