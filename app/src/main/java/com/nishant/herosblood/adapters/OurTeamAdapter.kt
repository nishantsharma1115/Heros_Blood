package com.nishant.herosblood.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.nishant.herosblood.R
import com.nishant.herosblood.models.Member

class OurTeamAdapter(
    private val membersList: List<Member>,
    val onButtonClick: (Pair<String, String>) -> Unit
) : RecyclerView.Adapter<OurTeamAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.txtMemberName)
        var title: TextView = itemView.findViewById(R.id.txtMemberTitle)
        var image: ImageView = itemView.findViewById(R.id.imgMember)
        var facebook: ImageView = itemView.findViewById(R.id.imgFacebook)
        var instagram: ImageView = itemView.findViewById(R.id.imgInstagram)
        var linkedin: ImageView = itemView.findViewById(R.id.imgLinkedin)
        var github: ImageView = itemView.findViewById(R.id.imgGithub)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_item_about, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val member = membersList[position]
        holder.apply {
            name.text = member.name
            title.text = member.title
            image.load(member.image)
            facebook.setOnClickListener {
                onButtonClick(Pair(member.facebook, "facebook"))
            }
            instagram.setOnClickListener {
                onButtonClick(Pair(member.instagram, "instagram"))
            }
            linkedin.setOnClickListener {
                onButtonClick(Pair(member.linkedin, "linkedin"))
            }
            github.setOnClickListener {
                onButtonClick(Pair(member.github, "github"))
            }
        }
    }

    override fun getItemCount(): Int {
        return membersList.size
    }
}
