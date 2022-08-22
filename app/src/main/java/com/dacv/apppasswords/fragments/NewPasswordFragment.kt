package com.dacv.apppasswords.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.dacv.apppasswords.databinding.FragmentNewPasswordBinding
import com.dacv.apppasswords.utils.File.Companion.KEY
import com.dacv.apppasswords.utils.File.Companion.encrypt
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*

class NewPasswordFragment : Fragment() {

    private lateinit var binding: FragmentNewPasswordBinding

    private lateinit var auth: FirebaseAuth

    private var db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = Firebase.auth
        // Inflate the layout for this fragment
        binding = FragmentNewPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAdd.setOnClickListener {
            if (validateTextInputLayout(binding.tfPassword) and
                    validateTextInputLayout(binding.tfEmail) and
                    validateTextInputLayout(binding.tfAccount))
                addAccountFirestore()
                //testing()
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
            "password" to encrypt(password,KEY),
            "image" to binding.tfImage.editText!!.text.toString().trim(),
        )
        val reference = db.collection("users").document(auth.currentUser!!.uid)
                reference.collection("passwords")
                    .document(id)
                    .set(datos)
                    .addOnCompleteListener {
                        binding.tfAccount.editText?.setText("")
                        binding.tfEmail.editText?.setText("")
                binding.tfPassword.editText?.setText("")
                binding.tfImage.editText?.setText("")
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

    private fun validateTextInputLayout(textInputLayout: TextInputLayout):Boolean{
        if (textInputLayout.editText?.text?.isEmpty() == true) {
            textInputLayout.error = "Este campo es obligatorio"
            return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}