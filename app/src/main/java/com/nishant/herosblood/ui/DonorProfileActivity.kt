package com.nishant.herosblood.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.nishant.herosblood.R
import com.nishant.herosblood.data.UserData
import com.nishant.herosblood.data.UserLocationData
import com.nishant.herosblood.databinding.ActivityDonorProfileBinding
import com.nishant.herosblood.util.Resource
import com.nishant.herosblood.viewmodels.LocationViewModel

class DonorProfileActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityDonorProfileBinding
    private lateinit var locationViewModel: LocationViewModel
    private var user: UserData = UserData()
    private lateinit var userLocationData: UserLocationData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_donor_profile)
        locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.donorMapLocation) as SupportMapFragment
        mapFragment.getMapAsync(this)

        user = intent.getSerializableExtra("UserData") as UserData
        binding.user = user

        locationViewModel.getUserLocation(FirebaseAuth.getInstance().currentUser?.uid.toString())
        binding.donorProfilePicture.load(user.profilePictureUrl) {
            this.placeholder(R.drawable.profile_none)
        }

        binding.expandMap.setOnClickListener {
            Intent(this, DonorLocationMapActivity::class.java).apply {
                this.putExtra("latitude", userLocationData.latitude?.toDouble())
                this.putExtra("longitude", userLocationData.longitude?.toDouble())
                this.putExtra("locality", userLocationData.locality)
                startActivity(this)
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        locationViewModel.getUserLocationStatus.observe(this, { response ->
            when (response) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    Log.d("Inside: ", "OnSuccess")
                    userLocationData = response.data as UserLocationData
                    Log.d("Longitude", userLocationData.longitude!!.toString())
                    Log.d("Latitude", userLocationData.latitude!!.toString())
                    val location = LatLng(
                        userLocationData.latitude!!.toDouble(),
                        userLocationData.longitude!!.toDouble()
                    )
                    map.addMarker(
                        MarkerOptions()
                            .position(location)
                            .title(userLocationData.locality)
                    )
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 20.0f))
                }
                is Resource.Error -> {

                }
            }
        })
    }
}