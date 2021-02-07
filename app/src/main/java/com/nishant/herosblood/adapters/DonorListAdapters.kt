package com.nishant.herosblood.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.nishant.herosblood.R
import com.nishant.herosblood.data.UserData
import com.nishant.herosblood.ui.DonorProfileActivity
import de.hdodenhof.circleimageview.CircleImageView
import java.io.Serializable

class DonorListAdapters(
    private val context: Context,
    private val donors: ArrayList<UserData>
) : RecyclerView.Adapter<DonorListAdapters.SingleDonorUser>() {
    class SingleDonorUser(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var background: ConstraintLayout = itemView.findViewById(R.id.donorListBackground)
        var profilePicture: CircleImageView = itemView.findViewById(R.id.profilePicture)
        var name: TextView = itemView.findViewById(R.id.name)
        var address: TextView = itemView.findViewById(R.id.address)
        var email: TextView = itemView.findViewById(R.id.email)
        var phone: TextView = itemView.findViewById(R.id.phone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleDonorUser {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.single_item_donor_list,
            parent,
            false
        )

        return SingleDonorUser(itemView)
    }

    override fun onBindViewHolder(holder: SingleDonorUser, position: Int) {
        val current = donors[position]

        holder.apply {
            this.profilePicture.load(current.profilePictureUrl)
            this.name.text = current.name
            this.address.text = current.fullAddress
            this.email.text = current.email
            this.phone.text = current.phoneNumber
        }

        holder.background.setOnClickListener {
            val intent = Intent(context, DonorProfileActivity::class.java)
            intent.putExtra("UserData", current as Serializable)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = donors.size
}