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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOtpVerificationBinding.bind(view)
        val email = args.email
        val otp = args.otp

        binding.btnSignUp.setOnClickListener {
            try {
                val currentOtp = binding.edtOtp.text.toString().toInt()
                if (currentOtp == otp) {
                    findNavController().navigate(
                        OtpVerificationFragmentDirections.actionOtpVerificationFragmentToNameAndPasswordFragment(
                            email
                        )
                    )
                } else {
                    binding.edtOtp.error = "OTP does not match"
                }
            } catch (nfe: NumberFormatException) {
                binding.edtOtp.error = "Please Input only Number"
            }
        }
    }
}