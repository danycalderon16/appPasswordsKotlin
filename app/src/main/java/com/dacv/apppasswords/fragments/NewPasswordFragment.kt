package com.dacv.apppasswords.fragments

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Base64.decode
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.dacv.apppasswords.databinding.FragmentNewPasswordBinding
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.util.Base64Utils.decode
import com.google.common.primitives.UnsignedLongs.decode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.lang.Byte.decode
import java.lang.Integer.decode
import java.math.BigInteger
import java.security.MessageDigest
import java.security.spec.PSSParameterSpec.DEFAULT
import java.text.DateFormat.DEFAULT
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.OAEPParameterSpec.DEFAULT

class NewPasswordFragment : Fragment() {

    private var _binding: FragmentNewPasswordBinding? = null

    private val binding get() = _binding!!

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    private var db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = Firebase.auth
        // Inflate the layout for this fragment
        _binding = FragmentNewPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var enc = ""
        binding.btnAdd.setOnClickListener {
            addAccountFirestore()
        }

        var decr = ""
        binding.decrypt.setOnClickListener {
            decr = binding.tfPassword.editText?.text.toString()
            Log.i("#### decr",decr.toString())
            val dec = md5(decr)
            if(enc == md5(decr)){
                Log.i("### match","Hubó coincidencia")
            }
        }
    }

    private fun addAccountFirestore() {
        val cal = GregorianCalendar.getInstance()

        var id = cal.get(Calendar.YEAR).toString()+
                cal.get(Calendar.MONTH).toString()+
                cal.get(Calendar.DAY_OF_MONTH).toString()+
                cal.get(Calendar.HOUR).toString()+
                cal.get(Calendar.MINUTE).toString()+
                cal.get(Calendar.SECOND).toString()+
                cal.get(Calendar.MILLISECOND).toString()

        val password = binding.tfPassword.editText?.text.toString().trim()

        val datos = hashMapOf(
            "id" to id,
            "account" to binding.tfAccount.editText!!.text.toString().trim(),
            "email" to binding.tfEmail.editText!!.text.toString().trim(),
            "password" to md5(password),
            "image" to binding.tfImage.editText!!.text.toString().trim(),
        )
        val reference = db.collection("users").document(auth.currentUser!!.uid)
        reference.collection("passwords")
            .document(id)
            .set(datos)
            .addOnCompleteListener {
                Toast.makeText(requireContext(), "Se agrego exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Atención")
                    .setMessage("Hubó un error")
                    .setPositiveButton("Cerrar",{d,i->})
                    .show()
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