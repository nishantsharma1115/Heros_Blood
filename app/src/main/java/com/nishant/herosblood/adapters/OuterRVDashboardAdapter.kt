package com.nishant.herosblood.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nishant.herosblood.R
import com.nishant.herosblood.data.UserData
import com.nishant.herosblood.ui.DonorListActivity
import kotlinx.android.synthetic.main.single_item_outer_rv_dashboard.view.*

class OuterRVDashboardAdapter(
    private val context: Context,
    private val bloodTypeList: List<String>,
    private var users: HashMap<String, ArrayList<UserData>>
) : RecyclerView.Adapter<OuterRVDashboardAdapter.BloodTypeHolder>() {

    class BloodTypeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bloodType: TextView = itemView.outerRVBloodType
        val innerRV: RecyclerView = itemView.innerRV
        val noUser: TextView = itemView.noUserFound
        var seeAll: TextView = itemView.rvSeeAll
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BloodTypeHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.single_item_outer_rv_dashboard,
            parent,
            false
        )
        return BloodTypeHolder(itemView)
    }

    override fun onBindViewHolder(holder: BloodTypeHolder, position: Int) {
        val currentBloodType = bloodTypeList[position]
        holder.bloodType.text = "$currentBloodType Donors"

        when (currentBloodType) {
            "A+" -> setInnerRvAdapter("A+", holder)
            "O-" -> setInnerRvAdapter("O-", holder)
            "O+" -> setInnerRvAdapter("O+", holder)
            "AB+" -> setInnerRvAdapter("AB+", holder)
            "AB-" -> setInnerRvAdapter("AB-", holder)
        }

        holder.seeAll.setOnClickListener {
            val intent = Intent(context, DonorListActivity::class.java)
            intent.putExtra("bloodType", currentBloodType)
            context.startActivity(intent)
        }

        holder.innerRV.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.innerRV.setHasFixedSize(true)
    }

    private fun setInnerRvAdapter(bloodType: String, holder: BloodTypeHolder) {
        if (users[bloodType]!!.size == 0) {
            holder.noUser.visibility = View.VISIBLE
        } else {
            holder.innerRV.adapter = InnerRVDashboardAdapter(context, users[bloodType]!!)
        }
    }

    override fun getItemCount(): Int = bloodTypeList.size
}