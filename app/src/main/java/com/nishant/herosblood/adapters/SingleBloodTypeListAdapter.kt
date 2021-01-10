package com.nishant.herosblood.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nishant.herosblood.R
import com.nishant.herosblood.data.UserData
import kotlinx.android.synthetic.main.single_user_dashboard.view.*

class SingleBloodTypeListAdapter(
    private var users: ArrayList<UserData>
) : RecyclerView.Adapter<SingleBloodTypeListAdapter.SingleBloodTypeUser>() {
    class SingleBloodTypeUser(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userName: TextView = itemView.userName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleBloodTypeUser {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.single_user_dashboard,
            parent,
            false
        )
        return SingleBloodTypeUser(itemView)
    }

    override fun onBindViewHolder(holder: SingleBloodTypeUser, position: Int) {
        val current = users[position]
        holder.userName.text = current.name
    }

    override fun getItemCount(): Int = users.size
}