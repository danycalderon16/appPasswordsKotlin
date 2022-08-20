package com.dacv.apppasswords.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dacv.apppasswords.R
import com.dacv.apppasswords.models.Account

class AccountAdapter (private val context: Context, private val list: ArrayList<Account>,itemListener:OnItemClickListener): RecyclerView.Adapter<AccountAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_account,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.account.text = list[position].account
        holder.email.text = list[position].email
        Glide.with(context)
            .load(list[position].image)
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        var account : TextView = view.findViewById(R.id.tv_name)
        var email : TextView = view.findViewById(R.id.tv_email)
        var image : ImageView = view.findViewById(R.id.img_logo)
    }
    interface OnItemClickListener{
        fun onItemClick(account: Account, i:Int)
    }
}
