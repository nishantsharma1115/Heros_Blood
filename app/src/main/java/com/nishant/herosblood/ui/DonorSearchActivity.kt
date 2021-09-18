package com.nishant.herosblood.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.nishant.herosblood.R
import com.nishant.herosblood.databinding.ActivityDonorSearchBinding
import com.nishant.herosblood.viewmodels.DataViewModel

class DonorSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonorSearchBinding
    private lateinit var dataViewModel: DataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonorSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)

        val bloodTypes = resources.getStringArray(R.array.blood_group)
        autoCompleteListAdapter(bloodTypes, binding.actvBloodGroup)

        binding.btnSave.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            val userId: String = user?.uid ?: ""
            val bloodType = binding.etBloodGroup.editText?.text.toString()
            val city = binding.edtCity.editText?.text.toString().trim()

            if (validateInputs(userId, city)) {
                val intent = Intent(this, DonorListActivity::class.java)
                intent.putExtra("from", "search")
                intent.putExtra("userId", userId)
                intent.putExtra("bloodType", bloodType)
                intent.putExtra("city", city)
                startActivity(intent)
            }
        }
    }

    private fun validateInputs(
        userId: String,
        city: String
    ): Boolean {
        if (userId.isEmpty()) {
            Toast.makeText(this, "User is logged out", Toast.LENGTH_SHORT).show()
            return false
        }
        if (city.isEmpty()) {
            binding.edtCity.error = "Required"
            return false
        }
        if (!isBloodGroupSelected()) {
            binding.etBloodGroup.error = "Required"
            return false
        }
        return true
    }

    private fun isBloodGroupSelected(): Boolean {
        return !(binding.etBloodGroup.editText?.text?.isEmpty() == true ||
                binding.etBloodGroup.editText?.text?.toString() == "Blood group")
    }

    private fun autoCompleteListAdapter(
        list: Array<String>,
        autoCompleteTextView: AutoCompleteTextView
    ) {
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, list)
        autoCompleteTextView.setAdapter(arrayAdapter)
    }

    private fun showLoadingBar() {
        binding.scrollLayout.visibility = View.GONE
        binding.searchAnimation.visibility = View.VISIBLE
    }

    private fun hideLoadingBar() {
        binding.scrollLayout.visibility = View.VISIBLE
        binding.searchAnimation.visibility = View.GONE
    }
}