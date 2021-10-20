package com.nishant.herosblood.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.nishant.herosblood.R
import com.nishant.herosblood.databinding.ActivityUserDashboardBinding
import com.nishant.herosblood.databinding.HeaderNavigationDrawerBinding
import com.nishant.herosblood.models.UserData
import com.nishant.herosblood.models.UserLocationData
import com.nishant.herosblood.ui.fragments.bottomsheet.DashboardBottomSheet
import com.nishant.herosblood.util.NearbyInfoWindowGoogleMap
import com.nishant.herosblood.util.Resource
import com.nishant.herosblood.util.location.LocationLiveData
import com.nishant.herosblood.util.location.LocationModel
import com.nishant.herosblood.viewmodels.DataViewModel
import com.nishant.herosblood.viewmodels.LocationViewModel
import java.io.Serializable

class UserDashboardActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityUserDashboardBinding
    private lateinit var headerBinding: HeaderNavigationDrawerBinding
    private lateinit var dataViewModel: DataViewModel
    private lateinit var locationViewModel: LocationViewModel
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var user: UserData = UserData()
    private lateinit var locationLiveData: LocationLiveData
    private var isDataReceived = false
    private lateinit var userLocationData: LocationModel
    override fun onResume() {
        requestLocation()

        val firebaseUser = mAuth.currentUser
        if (firebaseUser != null) {
            dataViewModel.readUserData(firebaseUser.uid)
        }

        super.onResume()
    }

    override fun onMapReady(map: GoogleMap) {

        dataViewModel.getFetchNearbyRequestsForDashboardStatus.observe(this, { response ->
            when (response) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    if (response.data != null) {
                        val nearbyRequestList = response.data

                        for (request in nearbyRequestList) {
                            val requestLocation = LatLng(
                                request.locationData.lat,
                                request.locationData.long
                            )

                            val markerOptions = MarkerOptions()
                                .position(requestLocation)
                                .title(request.fullName)
                                .snippet("Blood Request")

                            val customInfoWindow = NearbyInfoWindowGoogleMap(this)
                            map.setInfoWindowAdapter(customInfoWindow)
                            val marker = map.addMarker(markerOptions)
                            marker.tag = request
                        }
                    }
                }
                is Resource.Error -> {

                }
            }
        })

        locationLiveData.observe(this) {
            when (it) {
                is Resource.Error -> Unit
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    it.data?.let { location ->
                        userLocationData = location
                        val userLocation = LatLng(location.lat, location.long)
                        map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                userLocation,
                                12.0f
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.FullScreenTheme)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_dashboard)
        headerBinding = HeaderNavigationDrawerBinding.bind(binding.navigationView.getHeaderView(0))
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
        dataViewModel.fetchNearbyRequestForDashboard(mAuth.currentUser?.uid.toString())
        setUpUi()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.donorMapLocations) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationLiveData = LocationLiveData(this)
        observeLocationUpdates()

        binding.txtSeeAll.setOnClickListener {
            startActivity(Intent(this, NearbyBloodRequestsActivity::class.java))
        }

        dataViewModel.readUserDataStatus.observe(this, { response ->
            when (response) {
                is Resource.Loading -> {
                    startShimmerEffect()
                }
                is Resource.Success -> {
                    stopShimmerEffect()
                    isDataReceived = true
                    user = response.data as UserData
                    headerBinding.user = user
                    binding.user = user
                }
                is Resource.Error -> {
                    stopShimmerEffect()
                    Toast.makeText(
                        this,
                        response.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })

        binding.userProfilePicture.setOnClickListener {
            if (isDataReceived)
                moveToProfileActivity()
            else
                Toast.makeText(this, "Please wait..", Toast.LENGTH_SHORT).show()
        }
        binding.edtSearch2.setOnClickListener {
            startActivity(Intent(this, DonorSearchActivity::class.java))
        }
        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile -> {
                    if (isDataReceived)
                        moveToProfileActivity()
                    else
                        Toast.makeText(this, "Please wait..", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.requestForBlood -> {
                    Intent(this, RequestForBloodActivity::class.java).also { intent ->
                        intent.putExtra("UserLocationData", userLocationData as Serializable)
                        intent.putExtra("userId", mAuth.currentUser?.uid)
                        startActivity(intent)
                    }
                    true
                }
                R.id.previousBloodRequest -> {
                    startActivity(Intent(this, PreviousBloodRequestsActivity::class.java))
                    true
                }
                R.id.searchForBlood -> {
                    startActivity(Intent(this, DonorSearchActivity::class.java))
                    true
                }
                R.id.nearbyBloodRequests -> {
                    startActivity(Intent(this, NearbyBloodRequestsActivity::class.java))
                    true
                }
                R.id.ourTeam -> {
                    startActivity(Intent(this, OurTeamActivity::class.java))
                    true
                }
                R.id.tellAFriend -> {
                    shareAppIntent()
                    true
                }
                R.id.aboutUs -> {
                    startActivity(Intent(this, AboutUsActivity::class.java))
                    true
                }
                R.id.logout -> {
                    logoutUser()
                    true
                }
                else -> false
            }
        }
    }

    private fun shareAppIntent() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Heroes Blood")
        val url = " https://play.google.com/store/apps/details?id=com.nishant.herosblood"
        shareIntent.putExtra(Intent.EXTRA_TEXT, url)
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun moveToProfileActivity() {
        if (user.registered == "false") {
            supportFragmentManager.let { fragmentManager ->
                DashboardBottomSheet(user).apply {
                    show(fragmentManager, this.tag)
                }
            }
        } else {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }
    }

    private fun startShimmerEffect() {
        headerBinding.layoutShimmerEffect.visibility = View.VISIBLE
        headerBinding.layoutShimmerEffect.startShimmer()
    }

    private fun stopShimmerEffect() {
        headerBinding.layoutShimmerEffect.startShimmer()
        headerBinding.headerLayout.visibility = View.VISIBLE
        headerBinding.layoutShimmerEffect.visibility = View.GONE
    }

    private fun observeLocationUpdates() {
        locationLiveData.observe(this) {
            when (it) {
                is Resource.Error -> Unit
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    it.data?.let { location ->
                        setLocation(location)
                    }
                }
            }
        }
    }

    private fun setLocation(location: LocationModel) {
        val locationData = UserLocationData(
            mAuth.currentUser?.uid.toString(),
            location.lat.toString(),
            location.long.toString(),
            location.address,
            location.locality,
            location.area,
            location.country,
            location.pincode,
            location.featureName
        )
        binding.location = locationData.locality
        binding.currentLocation.visibility = View.VISIBLE
        locationViewModel.saveUserLocation(locationData)
    }

    private fun setUpUi() {
        setSupportActionBar(binding.toolbar)
        val drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.open,
            R.string.close
        )
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.drawer_icon)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onStop() {
        locationLiveData.stopListening()
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
            locationLiveData.startListening()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
        }
    }
}
