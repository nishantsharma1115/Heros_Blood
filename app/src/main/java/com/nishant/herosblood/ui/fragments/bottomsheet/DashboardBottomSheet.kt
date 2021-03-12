package com.nishant.herosblood.ui.fragments.bottomsheet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.nishant.herosblood.R
import com.nishant.herosblood.models.UserData
import com.nishant.herosblood.ui.LoginActivity
import com.nishant.herosblood.ui.UserRegistrationActivity
import kotlinx.android.synthetic.main.bottom_sheet_dashboard.*
import java.io.Serializable

class DashboardBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSheet()
    }

    private fun setUpSheet() {

        registerAsDonor.setOnClickListener {
            Intent(activity, UserRegistrationActivity::class.java).also { intent ->
                intent.putExtra("UserData", userData as Serializable)
                startActivity(intent)
            }
        }

        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    companion object {

        private var userData: UserData = UserData()

        @JvmStatic
        fun newInstance(bundle: Bundle, user: UserData): DashboardBottomSheet {
            val fragment = DashboardBottomSheet()
            fragment.arguments = bundle
            userData = user
            return fragment
        }
    }
}