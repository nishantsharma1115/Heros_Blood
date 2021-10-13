package com.nishant.herosblood.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.nishant.herosblood.databinding.ActivityNearbyBloodRequestsBinding
import com.nishant.herosblood.viewmodels.DataViewModel

class NearbyBloodRequestsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNearbyBloodRequestsBinding
    private lateinit var dataViewModel: DataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNearbyBloodRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        dataViewModel.fetchNearbyRequest(FirebaseAuth.getInstance().currentUser?.uid.toString())
    }
}