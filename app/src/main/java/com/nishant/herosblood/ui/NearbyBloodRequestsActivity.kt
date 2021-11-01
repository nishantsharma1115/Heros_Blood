package com.nishant.herosblood.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.nishant.herosblood.R
import com.nishant.herosblood.databinding.ActivityNearbyBloodRequestsBinding
import com.nishant.herosblood.util.NearbyInfoWindowGoogleMap
import com.nishant.herosblood.util.Resource
import com.nishant.herosblood.viewmodels.DataViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NearbyBloodRequestsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityNearbyBloodRequestsBinding
    private val dataViewModel by viewModels<DataViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNearbyBloodRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Nearby Blood Requests"
        dataViewModel.fetchNearbyRequest(FirebaseAuth.getInstance().currentUser?.uid.toString())
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.nearbyRequestLocations) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        dataViewModel.getFetchNearbyRequestsStatus.observe(this, { response ->
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
    }
}
