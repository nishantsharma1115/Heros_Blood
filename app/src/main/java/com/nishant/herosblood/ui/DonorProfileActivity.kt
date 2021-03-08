package com.nishant.herosblood.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.nishant.herosblood.R
import com.nishant.herosblood.databinding.ActivityDonorProfileBinding
import com.nishant.herosblood.models.UserData
import com.nishant.herosblood.models.UserLocationData
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

        locationViewModel.getUserLocation(user.userId!!)

        if (user.profilePictureUrl != null) {
            binding.donorProfilePicture.load(user.profilePictureUrl)
        }

        binding.expandMap.setOnClickListener {
            Intent(this, DonorLocationMapActivity::class.java).apply {
                this.putExtra("latitude", userLocationData.latitude?.toDouble())
                this.putExtra("longitude", userLocationData.longitude?.toDouble())
                this.putExtra("locality", userLocationData.locality)
                startActivity(this)
            }
        }

        binding.callDonor.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                makeCall()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    1
                )
            }
        }
        binding.emailDonor.setOnClickListener {
            sendMail()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            makeCall()
        }
    }

    private fun makeCall() {
        Intent(Intent.ACTION_CALL).also {
            it.data = Uri.parse("tel:" + user.phoneNumber)
            startActivity(it)
        }
    }

    private fun sendMail() {
        Intent(Intent.ACTION_SEND).also {
            it.putExtra(Intent.EXTRA_EMAIL, user.email)
            it.type = "message/rfc822"
            startActivity(Intent.createChooser(it, "Select eMail"))
        }
    }

    override fun onMapReady(map: GoogleMap) {
        locationViewModel.getUserLocationStatus.observe(this, { response ->
            when (response) {
                is Resource.Loading -> {
                    Log.d("Loading", "Fetching Map Data")
                }
                is Resource.Success -> {
                    userLocationData = response.data as UserLocationData
                    binding.liveLocation = userLocationData.addressLine
                    val location = LatLng(
                        userLocationData.latitude!!.toDouble(),
                        userLocationData.longitude!!.toDouble()
                    )
                    map.addMarker(
                        MarkerOptions()
                            .position(location)
                            .title(userLocationData.locality)
                    )
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12.0f))
                }
                is Resource.Error -> {
                    Log.d("Error", "An Error occurred")
                }
            }
        })
    }
}