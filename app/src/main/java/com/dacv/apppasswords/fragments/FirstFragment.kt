package com.dacv.apppasswords.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.dacv.apppasswords.R
import com.dacv.apppasswords.adapters.AccountAdapter
import com.dacv.apppasswords.databinding.FragmentFirstBinding
import com.dacv.apppasswords.models.Account
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */


class FirstFragment : Fragment() {

    private lateinit var binding: FragmentFirstBinding

    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    var accounts = ArrayList<Account>()
    lateinit var myAdapter: AccountAdapter
    lateinit var rv: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv = binding.rv
        auth = Firebase.auth

        val message = arguments?.getBoolean("message")

        if (message == true){
            Snackbar.make(binding.cordiFirst,"Se elimino correctamente", Snackbar.LENGTH_SHORT).show()
        }

        val query = db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("passwords")
            .orderBy("account")

        val options =
            FirestoreRecyclerOptions.Builder<Account>().setQuery(query, Account::class.java)
                .setLifecycleOwner(this).build()

        val adapter = object : FirestoreRecyclerAdapter<Account, AccountAdapter>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountAdapter {
                val v: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_account, parent, false)
                return AccountAdapter(v)
            }

            override fun onBindViewHolder(holder: AccountAdapter, position: Int, model: Account) {
                var account: TextView = holder.itemView.findViewById(R.id.tv_name)
                var email: TextView = holder.itemView.findViewById(R.id.tv_email)
                var image: ImageView = holder.itemView.findViewById(R.id.img_logo)

                account.text = model.account
                email.text = model.email
                Glide.with(requireContext())
                    .load(model.image)
                    .transform(RoundedCorners(40))
                    .into(image)

                holder.itemView.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putSerializable("id", model)
                    findNavController().navigate(
                        R.id.action_FirstFragment_to_SecondFragment,
                        bundle
                    )
                }


            }
        }


        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(requireContext())

        binding.fab.setOnClickListener {

            findNavController().navigate(R.id.action_FirstFragment_to_newPasswordFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}

