package com.nishant.herosblood.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.nishant.herosblood.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DonorLocationMapActivity : AppCompatActivity(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_location_map)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val latitude = intent.getDoubleExtra("latitude", 0.0) as Double
        val longitude = intent.getDoubleExtra("longitude", 0.0) as Double
        val locality = intent.getStringExtra("locality") as String
        val location = LatLng(latitude, longitude)
        Log.d("MapItems", longitude.toString())
        Log.d("MapItems", latitude.toString())
        Log.d("MapItems", locality)
        googleMap.addMarker(MarkerOptions().position(location).title(locality))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12.0f))
    }
}
