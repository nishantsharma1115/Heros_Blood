package com.nishant.herosblood.adapters.dashboard

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.nishant.herosblood.R
import com.nishant.herosblood.models.UserData
import com.nishant.herosblood.ui.DonorProfileActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.single_item_inner_rv_dashboard.view.*
import java.io.Serializable

class InnerRvAdapter(
    private var context: Context,
    private var users: ArrayList<UserData>
) : RecyclerView.Adapter<InnerRvAdapter.SingleBloodTypeUser>() {
    class SingleBloodTypeUser(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userName: TextView = itemView.userName
        var profilePicture: ImageView = itemView.userProfilePicture
        var background: ConstraintLayout = itemView.layout_background
        var userLocation: TextView = itemView.userLocation
        var availabilityStatus: CircleImageView = itemView.availabilityStatus
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleBloodTypeUser {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.single_item_inner_rv_dashboard,
            parent,
            false
        )
        return SingleBloodTypeUser(itemView)
    }

    override fun onBindViewHolder(holder: SingleBloodTypeUser, position: Int) {
        val current = users[position]
        holder.userName.text = current.name
        holder.userLocation.text = current.city

        if (current.profilePictureUrl != null) {
            holder.profilePicture.load(current.profilePictureUrl) {
                this.placeholder(R.drawable.profile_none)
            }
        }

        if (!current.available.toBoolean()) {
            holder.availabilityStatus.setImageResource(R.color.greyColor)
        } else {
            holder.availabilityStatus.setImageResource(R.color.green)
        }

        holder.background.setOnClickListener {
            val intent = Intent(context, DonorProfileActivity::class.java)
            intent.putExtra("UserData", current as Serializable)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = users.size
}