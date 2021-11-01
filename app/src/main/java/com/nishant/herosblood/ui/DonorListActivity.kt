package com.nishant.herosblood.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.nishant.herosblood.R
import com.nishant.herosblood.adapters.DonorListAdapters
import com.nishant.herosblood.databinding.ActivityDonorListBinding
import com.nishant.herosblood.models.UserData
import com.nishant.herosblood.util.Resource
import com.nishant.herosblood.viewmodels.DataViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DonorListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonorListBinding
    private val dataViewModel by viewModels<DataViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_donor_list)

        val from = intent.getStringExtra("from") as String
        if (from == "Dashboard") {
            val bloodType = intent.getStringExtra("bloodType") as String
            dataViewModel.getDonorList(
                FirebaseAuth.getInstance().currentUser?.uid.toString(),
                bloodType
            )
        } else if (from == "search") {
            val userId = intent.getStringExtra("userId") as String
            val bloodType = intent.getStringExtra("bloodType") as String
            val city = intent.getStringExtra("city") as String
            dataViewModel.getSearchDonorList(userId, bloodType, city)
        }
        dataViewModel.getSearchDonorStatus.observe(this, { response -> showDataToRv(response) })
        dataViewModel.getDonorListStatus.observe(this, { response -> showDataToRv(response) })
    }

    private fun showDataToRv(response: Resource<ArrayList<UserData>>) {
        when (response) {
            is Resource.Loading -> {
                binding.shimmerLayoutDonorList.startShimmer()
            }
            is Resource.Success -> {
                if (response.data?.size!! <= 0) {
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
                binding.shimmerLayoutDonorList.visibility = View.GONE
                Toast.makeText(this, "Check Internet Connection", Toast.LENGTH_LONG).show()
            }
        }
    }
}
