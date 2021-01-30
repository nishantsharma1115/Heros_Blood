package com.nishant.herosblood.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nishant.herosblood.R
import com.nishant.herosblood.adapters.DonorListAdapters
import com.nishant.herosblood.databinding.ActivityDonorListBinding
import com.nishant.herosblood.util.Resource
import com.nishant.herosblood.viewmodels.DataViewModel

class DonorListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonorListBinding
    private lateinit var dataViewModel: DataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_donor_list)
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)

        val bloodType = intent.getStringExtra("bloodType") as String
        dataViewModel.getDonorList(bloodType)

        dataViewModel.getDonorListStatus.observe(this, { response ->
            when (response) {
                is Resource.Loading -> {
                    binding.shimmerLayoutDonorList.startShimmer()
                }
                is Resource.Success -> {
                    if (response.data == null) {
                        binding.noDonor.visibility = View.VISIBLE
                        binding.shimmerLayoutDonorList.stopShimmer()
                        binding.shimmerLayoutDonorList.visibility = View.GONE
                        binding.noDonor.visibility = View.VISIBLE
                    } else {
                        binding.rvDonorList.adapter = DonorListAdapters(this, response.data)
                        binding.rvDonorList.layoutManager = LinearLayoutManager(this)
                        binding.rvDonorList.setHasFixedSize(true)
                        binding.shimmerLayoutDonorList.stopShimmer()
                        binding.shimmerLayoutDonorList.visibility = View.GONE
                        binding.rvDonorList.visibility = View.VISIBLE
                    }
                }
                is Resource.Error -> {
                    binding.shimmerLayoutDonorList.stopShimmer()
                    Toast.makeText(this, "Check Internet Connection", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}