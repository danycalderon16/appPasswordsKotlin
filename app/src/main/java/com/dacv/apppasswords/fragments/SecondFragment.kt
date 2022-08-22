package com.dacv.apppasswords.fragments

import android.content.Intent
import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.dacv.apppasswords.R
import com.dacv.apppasswords.databinding.FragmentSecondBinding
import com.dacv.apppasswords.models.Account
import com.dacv.apppasswords.utils.File.Companion.KEY
import com.dacv.apppasswords.utils.File.Companion.decryptWithAES
import com.dacv.apppasswords.utils.File.Companion.encrypt
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

    private lateinit var binding: FragmentSecondBinding

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private lateinit var account:Account

    private var counter = 0

    private var TYPE = 0

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
                    when (TYPE){
                        DELETE ->{
                            AlertDialog.Builder(requireContext())
                                .setMessage("¿Está seguro de eliminar esta cuenta?")
                                .setPositiveButton("Sí",{d,i->deleteAccount()})
                                .setNegativeButton("Cancelar",{d,i->d.dismiss()})
                                .show()
                        }
                        EDIT ->{
                            val bundle = Bundle()
                            bundle.putSerializable("id", account)
                            findNavController().navigate(
                                R.id.action_SecondFragment_to_editFragment,
                                bundle
                            )
                        }
                        CHECK ->{
                            val pass = binding.tfDetailsAccount.editText?.text.toString()
                            val passEncrypted = encrypt(pass,KEY)
                            if (decryptWithAES(KEY,passEncrypted) == (account.password)) {
                            //if (function(pass) == (account.password)) {
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
                        VIEW ->{
                            val pass = binding.tfDetailsAccount.editText?.text.toString()
                            val passEncrypted = encrypt(account.password,KEY)
                            val dec =  decryptWithAES(KEY,account.password)
                            Log.i("TEST enc", passEncrypted.toString())
                            Log.i("TEST dec", dec.toString())
                            binding.textView.text = decryptWithAES(KEY,account.password)

                            val timer = object: CountDownTimer(1500, 1000) {
                                override fun onTick(millisUntilFinished: Long) {
                                }
                                override fun onFinish() {
                                    binding.textView.text = ""
                                }
                            }
                            timer.start()
                        }
                    }
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
        binding = FragmentSecondBinding.inflate(inflater, container, false)
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
            TYPE = CHECK
            biometricPrompt.authenticate(promptInfo)
        }

        binding.buttonEdit.setOnClickListener {
            TYPE = EDIT
            biometricPrompt.authenticate(promptInfo)
        }

        binding.buttonDelete.setOnClickListener {
            TYPE = DELETE
            biometricPrompt.authenticate(promptInfo)
        }

        binding.imDetailsLogo.setOnClickListener {
            if(counter==10) {
                TYPE = VIEW
                biometricPrompt.authenticate(promptInfo)
                counter = 0
            }
            counter++
            Log.i("counter",""+counter)
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
    }

    companion object {
        private const val EDIT = 1001
        private const val CHECK = 1002
        private const val DELETE = 1003
        private const val VIEW = 1004

    }
}