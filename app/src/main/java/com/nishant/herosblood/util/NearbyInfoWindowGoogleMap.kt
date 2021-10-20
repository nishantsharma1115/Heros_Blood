package com.nishant.herosblood.util

import android.app.Activity
import android.content.Context
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.nishant.herosblood.R
import com.nishant.herosblood.databinding.NearbyInfoWindowGoogleMapBinding
import com.nishant.herosblood.models.BloodRequestData
import kotlinx.android.synthetic.main.nearby_info_window_google_map.view.*


class NearbyInfoWindowGoogleMap(val context: Context) : GoogleMap.InfoWindowAdapter {

    private lateinit var binding: NearbyInfoWindowGoogleMapBinding

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View {

        val mInfoView = (context as Activity).layoutInflater.inflate(
            R.layout.nearby_info_window_google_map,
            null
        )
        val mInfoWindow = marker.tag as BloodRequestData
        mInfoView.txtBloodGroup.text = mInfoWindow.bloodGroup
        mInfoView.txtName.text = mInfoWindow.fullName
        mInfoView.txtUnit.text =
            "${mInfoWindow.unitOfBloodRequired} Unit (${mInfoWindow.bloodGroup} Blood Group)"
        mInfoView.txtAddress.text = mInfoWindow.address
        mInfoView.txtMobile.text = mInfoWindow.phoneNumber

        if (mInfoWindow.isCritical.toBoolean()) {
            mInfoView.txtCritical.visibility = View.VISIBLE
        } else {
            mInfoView.txtCritical.visibility = View.GONE
        }

        return mInfoView

//        binding = DataBindingUtil.inflate(
//            LayoutInflater.from(context),
//            R.layout.nearby_info_window_google_map,
//            null,
//            true
//        )
//        val infoData: BloodRequestData = marker.tag as BloodRequestData
//        Log.d("Testing #1", infoData.toString())
//        binding.bloodRequest = infoData
//        Log.d("Testing #2", binding.bloodRequest.toString())
//        return binding.root
    }
}