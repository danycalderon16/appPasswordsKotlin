package com.dacv.apppasswords.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dacv.apppasswords.R
import com.dacv.apppasswords.databinding.FragmentEditBinding
import com.dacv.apppasswords.databinding.FragmentNewPasswordBinding
import com.dacv.apppasswords.models.Account
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}