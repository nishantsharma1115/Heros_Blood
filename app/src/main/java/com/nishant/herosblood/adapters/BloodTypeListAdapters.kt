package com.nishant.herosblood.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nishant.herosblood.R
import com.nishant.herosblood.data.UserData
import kotlinx.android.synthetic.main.item_dashboard_types_of_blood_group.view.*

class BloodTypeListAdapters(
    private val context: Context,
    private val bloodTypeList: List<String>,
    private var users: HashMap<String, ArrayList<UserData>>
) : RecyclerView.Adapter<BloodTypeListAdapters.BloodTypeHolder>() {

    class BloodTypeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bloodType: TextView = itemView.outerRVBloodType
        val innerRV: RecyclerView = itemView.innerRV
        val noUser: TextView = itemView.noUserFound
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BloodTypeHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_dashboard_types_of_blood_group,
            parent,
            false
        )
        return BloodTypeHolder(itemView)
    }

    override fun onBindViewHolder(holder: BloodTypeHolder, position: Int) {
        val currentBloodType = bloodTypeList[position]
        holder.bloodType.text = "$currentBloodType Donors"
        if (currentBloodType == "A+") {
            if (users["A+"]!!.size == 0) {
                holder.noUser.visibility = View.VISIBLE
            }
        }
        if (currentBloodType == "O-") {
            if (users["O-"]!!.size == 0) {
                holder.noUser.visibility = View.VISIBLE
            }
        }
        if (currentBloodType == "O+") {
            if (users["O+"]!!.size == 0) {
                holder.noUser.visibility = View.VISIBLE
            } else {
                holder.innerRV.adapter = SingleBloodTypeListAdapter(users["O+"]!!)
            }
        }
        if (currentBloodType == "AB+") {
            if (users["AB+"]!!.size == 0) {
                holder.noUser.visibility = View.VISIBLE
            }
        }
        if (currentBloodType == "AB-") {
            if (users["AB-"]!!.size == 0) {
                holder.noUser.visibility = View.VISIBLE
            }
        }
        holder.innerRV.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.innerRV.setHasFixedSize(true)
    }

    override fun getItemCount(): Int = bloodTypeList.size
}