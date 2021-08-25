package com.nishant.herosblood.ui.fragments.signup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nishant.herosblood.R
import com.nishant.herosblood.databinding.FragmentOtpVerificationBinding

class OtpVerificationFragment : Fragment(R.layout.fragment_otp_verification) {

    private lateinit var binding: FragmentOtpVerificationBinding
    private val args: OtpVerificationFragmentArgs by navArgs()
    private var currentOtp: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOtpVerificationBinding.bind(view)
        val email = args.email
        val otp = args.otp

        binding.email = email

        binding.otpView.setOtpCompletionListener { userOtp ->
            currentOtp = userOtp.toInt()
            if (currentOtp == otp) {
                findNavController().navigate(
                    OtpVerificationFragmentDirections.actionOtpVerificationFragmentToNameAndPasswordFragment(
                        email
                    )
                )
            } else {
                binding.otpView.error = "OTP does not match"
            }
        }

        binding.btnVerify.setOnClickListener {

            if (currentOtp == -1 && currentOtp.toString().length < 6) {
                binding.otpView.error = "Please enter OTP"
            } else if (currentOtp == otp) {
                findNavController().navigate(
                    OtpVerificationFragmentDirections.actionOtpVerificationFragmentToNameAndPasswordFragment(
                        email
                    )
                )
            } else {
                binding.otpView.error = "OTP does not match"
            }
        }
    }
}