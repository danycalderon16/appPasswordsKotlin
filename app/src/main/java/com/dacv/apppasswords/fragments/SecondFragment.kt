package com.dacv.apppasswords.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.dacv.apppasswords.R
import com.dacv.apppasswords.databinding.FragmentSecondBinding
import com.dacv.apppasswords.models.Account
import com.dacv.apppasswords.utils.File.Companion.function
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.concurrent.Executor


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

    private lateinit var account:Account


    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = Firebase.auth

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
                    val pass = binding.tfDetailsAccount.editText?.text.toString()
                    if (function(pass) == (account.password)) {
                        Toast.makeText(requireContext(), "Esa es su contraseña", Toast.LENGTH_SHORT).show()
                        val timer = object: CountDownTimer(5000, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                            }
                            override fun onFinish() {
                                binding.tfDetailsAccount.editText?.setText("")
                            }
                        }
                        timer.start()
                    }else
                        Toast.makeText(requireContext(), "Contraseña incorrecta", Toast.LENGTH_SHORT).show()

                }
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(requireContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Confirme su identidad")
            .setSubtitle("Ingrese su credencial biométrica")
            .setNegativeButtonText("Cancelar")
            .build()
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        account = requireArguments().getSerializable("id") as Account

        (activity as AppCompatActivity).supportActionBar?.title = account.account

        requireActivity().title = account.account
        binding.tvDetailsEmail.text = account.email
        Glide.with(requireContext())
            .load(account.image)
            .transform(RoundedCorners(20))
            .placeholder(R.drawable.ic_baseline_broken_image_24)
            .into(binding.imDetailsLogo)

        binding.buttonCheck.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }

        binding.buttonEdit.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("id", account)
            findNavController().navigate(
                R.id.action_SecondFragment_to_editFragment,
                bundle
            )
        }

        binding.buttonDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage("¿Está seguro de eliminar esta cuenta?")
                .setPositiveButton("Sí",{d,i->deleteAccount()})
                .setNegativeButton("Cancelar",{d,i->d.dismiss()})
                .show()
        }
    }

    private fun deleteAccount() {
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("passwords")
            .document(account.id)
            .delete()
            .addOnCompleteListener {
                val bundle = Bundle()
                bundle.putBoolean("message", true)
                findNavController().navigate(
                    R.id.action_SecondFragment_to_FirstFragment,
                    bundle
                )
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Hubo un error", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}