package com.husnul23.githubsubmission

import com.bumptech.glide.Glide
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions

class ListUsersAdapter : RecyclerView.Adapter<ListUsersAdapter.ListViewHolder>() {
    var listUser = mutableListOf<Github>()

    fun setUsers(users: List<Github>) {
        listUser = users.toMutableList()
        notifyDataSetChanged()
    }

    fun addUsers(users: List<Github>) {
        listUser.addAll(users)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListUsersAdapter.ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_user, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListUsersAdapter.ListViewHolder, position: Int) {
        val github = listUser[position]
        holder.bind(github)
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

    inner class ListViewHolder(itemView: View ) : RecyclerView.ViewHolder(itemView) {
        private var githubUsername: TextView = itemView.findViewById(R.id.textUsername)
        private var githubAvatar: ImageView = itemView.findViewById(R.id.img_item_photo)

        fun bind(github: Github) {

            Glide.with(itemView.context)
                .load(github.avatar)
                .apply(RequestOptions().override(60, 70))
                .into(githubAvatar)

            githubUsername.text = github.username
        }
    }
}