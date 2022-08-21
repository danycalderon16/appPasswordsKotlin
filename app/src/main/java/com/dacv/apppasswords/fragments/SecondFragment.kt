package com.dacv.apppasswords.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.dacv.apppasswords.R
import com.dacv.apppasswords.databinding.FragmentSecondBinding
import com.dacv.apppasswords.models.Account
import java.math.BigInteger
import java.security.MessageDigest
import java.util.concurrent.Executor

import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(requireContext(),
                        "Authentication error: $errString", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(requireContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(requireContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val account:Account = requireArguments().getSerializable("id") as Account

        Log.i("ID!!!!!",id.toString())

        (activity as AppCompatActivity).supportActionBar?.title = account.account

        requireActivity().title = account.account
        binding.tvDetailsEmail.text = "${account.email}"
        Glide.with(requireContext())
            .load(account.image)
            .transform(RoundedCorners(20))
            .into(binding.imDetailsLogo)

        binding.buttonCheck.setOnClickListener {
            val pass = binding.tfDetailsAccount.editText?.text.toString()
            biometricPrompt.authenticate(promptInfo)
            //if (md5(pass) == (account.password)) {


                // Prompt appears when user clicks "Log in".
                // Consider integrating with the keystore to unlock cryptographic operations,
                // if needed by your app.

            //}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}