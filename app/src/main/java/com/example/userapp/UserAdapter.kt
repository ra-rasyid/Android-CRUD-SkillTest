package com.example.userapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class UserAdapter(
    private val list: MutableList<User>, // Gunakan MutableList agar bisa diedit
    private val onEdit: (User, Int) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvName)
        val email: TextView = view.findViewById(R.id.tvEmail)
        val avatar: ImageView = view.findViewById(R.id.imgAvatar)
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = list[position]
        holder.name.text = user.name
        holder.email.text = user.email

        Glide.with(holder.itemView.context)
            .load("https://i.pravatar.cc/150?img=${user.id}")
            .into(holder.avatar)

        // Klik Item untuk Update
        holder.itemView.setOnClickListener { onEdit(user, position) }

        // Klik Icon untuk Delete
        holder.btnDelete.setOnClickListener { onDelete(position) }
    }

    override fun getItemCount(): Int = list.size
}