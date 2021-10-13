package com.nishant.herosblood.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.nishant.herosblood.adapters.PreviousBloodRequestAdapter
import com.nishant.herosblood.databinding.ActivityPreviousBloodRequestsBinding
import com.nishant.herosblood.util.Resource
import com.nishant.herosblood.viewmodels.DataViewModel

class PreviousBloodRequestsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreviousBloodRequestsBinding
    private lateinit var dataViewModel: DataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviousBloodRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
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