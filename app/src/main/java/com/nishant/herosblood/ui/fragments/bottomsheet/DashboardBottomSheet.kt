package com.nishant.herosblood.ui.fragments.bottomsheet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.nishant.herosblood.databinding.BottomSheetDashboardBinding
import com.nishant.herosblood.models.UserData
import com.nishant.herosblood.ui.LoginActivity
import com.nishant.herosblood.ui.UserRegistrationActivity
import java.io.Serializable

class DashboardBottomSheet(
    private val userData: UserData
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.userData = userData
        setUpSheet()
    }

    private fun setUpSheet() {

        binding.registerAsDonor.setOnClickListener {
            Intent(activity, UserRegistrationActivity::class.java).also { intent ->
                intent.putExtra("UserData", userData as Serializable)
                startActivity(intent)
            }
        }

        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
