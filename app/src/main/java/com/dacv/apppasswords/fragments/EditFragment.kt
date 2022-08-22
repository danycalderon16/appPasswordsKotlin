package com.dacv.apppasswords.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dacv.apppasswords.R
import com.dacv.apppasswords.databinding.FragmentEditBinding
import com.dacv.apppasswords.models.Account
import com.dacv.apppasswords.utils.File.Companion.KEY
import com.dacv.apppasswords.utils.File.Companion.encrypt
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null

    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private var db = FirebaseFirestore.getInstance()

    private lateinit var account: Account

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        auth = Firebase.auth
        // Inflate the layout for this fragment
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        account = requireArguments().getSerializable("id") as Account

        binding.tfAccount.editText?.setText(account.account)
        binding.tfEmail.editText?.setText(account.email)
        binding.tfImage.editText?.setText(account.image)

        binding.btnUpdate.setOnClickListener {
            updateAccount()
        }
    }

    private fun updateAccount() {
        val password = binding.tfPassword.editText?.text.toString().trim()

        val datos = mapOf(
            "id" to account.id,
            "account" to binding.tfAccount.editText!!.text.toString().trim(),
            "email" to binding.tfEmail.editText!!.text.toString().trim(),
            "password" to encrypt(password,KEY),
            "image" to binding.tfImage.editText!!.text.toString().trim(),
        )
        val reference = db.collection("users").document(auth.currentUser!!.uid)
        reference.collection("passwords")
            .document(account.id)
            .update(datos)
            .addOnCompleteListener {
                binding.tfAccount.editText?.setText("")
                binding.tfEmail.editText?.setText("")
                binding.tfPassword.editText?.setText("")
                binding.tfImage.editText?.setText("")
                Toast.makeText(requireContext(), "Se actualizo exitosamente", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_editFragment_to_FirstFragment)
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

}