package com.nishant.herosblood.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.nishant.herosblood.adapters.PreviousBloodRequestAdapter
import com.nishant.herosblood.databinding.ActivityPreviousBloodRequestsBinding
import com.nishant.herosblood.util.Resource
import com.nishant.herosblood.viewmodels.DataViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreviousBloodRequestsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreviousBloodRequestsBinding
    private val dataViewModel by viewModels<DataViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviousBloodRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseAuth.getInstance().currentUser?.uid?.let {
            dataViewModel.fetchPreviousBloodRequest(it)
        }
        dataViewModel.getFetchPreviousBloodRequestStatus.observe(this, { response ->
            when (response) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    val adapter = PreviousBloodRequestAdapter()
                    binding.rvPreviousBloodRequests.adapter = adapter
                    binding.rvPreviousBloodRequests.layoutManager =
                        LinearLayoutManager(applicationContext)
                    binding.rvPreviousBloodRequests.setHasFixedSize(true)

                    if (response.data != null) {
                        adapter.submitList(response.data)
                    }
                }
                is Resource.Error -> {
                }
            }
        })
    }
}
