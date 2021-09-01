package com.nishant.herosblood.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.nishant.herosblood.R
import com.nishant.herosblood.adapters.dashboard.OuterRvAdapter
import com.nishant.herosblood.databinding.ActivityUserDashboardBinding
import com.nishant.herosblood.models.UserData
import com.nishant.herosblood.models.UserLocationData
import com.nishant.herosblood.ui.fragments.bottomsheet.DashboardBottomSheet
import com.nishant.herosblood.util.Resource
import com.nishant.herosblood.viewmodels.DataViewModel
import com.nishant.herosblood.viewmodels.LocationViewModel
import java.io.Serializable
import java.util.*

class UserDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDashboardBinding
    private lateinit var dataViewModel: DataViewModel
    private lateinit var locationViewModel: LocationViewModel
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var user: UserData = UserData()
    private var isDataReceived: Boolean = false
    private lateinit var locationManager: LocationManager

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val geoCoder = Geocoder(applicationContext, Locale.getDefault())
            val address = geoCoder.getFromLocation(location.latitude, location.longitude, 1)

            val locationData = UserLocationData(
                mAuth.currentUser?.uid.toString(),
                location.latitude.toString(),
                location.longitude.toString(),
                address[0].getAddressLine(0),
                address[0].locality,
                address[0].adminArea,
                address[0].countryName,
                address[0].postalCode,
                address[0].featureName
            )
            binding.location = locationData.locality
            locationViewModel.saveUserLocation(locationData)
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
        override fun onProviderEnabled(p0: String?) {}
        override fun onProviderDisabled(p0: String?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.FullScreenTheme)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_dashboard)
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
        val bloodTypeList = resources.getStringArray(R.array.blood_group).toList()

        dataViewModel.getAllDonors(mAuth.currentUser?.uid.toString())
        dataViewModel.getAllDonorsStatus.observe(this, { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoadingBar()
                }
                is Resource.Success -> {
                    hideLoadingBar()
                    if (response.data != null) {
                        binding.rvDonorListUserDashboard.adapter =
                            OuterRvAdapter(this, bloodTypeList, response.data)
                        binding.rvDonorListUserDashboard.layoutManager = LinearLayoutManager(this)
                        binding.rvDonorListUserDashboard.setHasFixedSize(true)
                    } else {
                        Toast.makeText(this, "Donor List Data is Null", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Error -> {
                    hideLoadingBar()
                    Toast.makeText(this, "Check Internet Connection", Toast.LENGTH_LONG).show()
                }
            }
        })

        if (!user.registered.isNullOrEmpty() && user.registered == "false") {
            binding.registrationStatus.setOnClickListener {
                if (isDataReceived) {
                    Intent(this, UserRegistrationActivity::class.java).also { intent ->
                        intent.putExtra("UserData", user as Serializable)
                        startActivity(intent)
                    }
                }
            }
        }

        dataViewModel.readUserDataStatus.observe(this, { response ->
            when (response) {
                is Resource.Loading -> {
                    binding.layoutShimmerEffect.startShimmer()
                }
                is Resource.Success -> {
                    user = response.data as UserData
                    isDataReceived = true
                    binding.layoutUserDetails.visibility = View.VISIBLE
                    binding.user = user
                    binding.layoutShimmerEffect.stopShimmer()
                    binding.layoutShimmerEffect.visibility = View.GONE
                    user.let {
                        if (it.registered == "false") {
                            binding.registrationStatus.text = "Want to be a donor? Click Here"
                        } else if (it.registered == "true") {
                            binding.registrationStatus.text = "Registered as a Donor"
                            binding.registrationStatus.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.verified_icon,
                                0
                            )
                        }
                    }
                    if (user.profilePictureUrl.equals("")) {
                        binding.imgProfilePicture.load(R.drawable.profile_none)
                    } else {
                        binding.imgProfilePicture.load(user.profilePictureUrl)
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(
                        this,
                        response.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })

        binding.imgProfilePicture.setOnClickListener {
            if (user.registered == "false") {
                supportFragmentManager.let { fragmentManager ->
                    DashboardBottomSheet().apply {
                        show(fragmentManager, tag)
                    }
                }
            } else {
                startActivity(Intent(this, UserProfileActivity::class.java))
            }
        }
    }

    override fun onResume() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        requestLocation()

        val firebaseUser = mAuth.currentUser
        if (firebaseUser != null) {
            dataViewModel.readUserData(firebaseUser.uid)
        }

        super.onResume()
    }

    override fun onStop() {
        locationManager.removeUpdates(locationListener)
        super.onStop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocation()
        }
    }

    private fun requestLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                600000L,
                1000F,
                locationListener
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }

    private fun showLoadingBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingBar() {
        binding.progressBar.visibility = View.GONE
        binding.rvDonorListUserDashboard.visibility = View.VISIBLE
    }

}