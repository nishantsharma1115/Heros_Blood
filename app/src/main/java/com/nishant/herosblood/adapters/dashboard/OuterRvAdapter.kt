package com.nishant.herosblood.adapters.dashboard

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nishant.herosblood.R
import com.nishant.herosblood.ui.DonorListActivity
import com.nishant.herosblood.util.DifferentDonorLists
import kotlinx.android.synthetic.main.single_item_outer_rv_dashboard.view.*

class OuterRvAdapter(
    private val context: Context,
    private val bloodTypeList: List<String>,
    private var donors: DifferentDonorLists
) : RecyclerView.Adapter<OuterRvAdapter.BloodTypeHolder>() {

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

        setInnerAdapter(currentBloodType, holder)

        holder.seeAll.setOnClickListener {
            val intent = Intent(context, DonorListActivity::class.java)
            intent.putExtra("bloodType", currentBloodType)
            context.startActivity(intent)
        }

        holder.innerRV.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        holder.innerRV.setHasFixedSize(true)
    }

    private fun setInnerAdapter(bloodType: String, holder: BloodTypeHolder) {
        when (bloodType) {
            "A+" -> {
                if (donors.aPositiveDonors.size == 0) {
                    holder.noUser.visibility = View.VISIBLE
                } else {
                    holder.innerRV.adapter = InnerRvAdapter(context, donors.aPositiveDonors)
                }
            }
            "A-" -> {
                if (donors.aNegativeDonors.size == 0) {
                    holder.noUser.visibility = View.VISIBLE
                } else {
                    holder.innerRV.adapter = InnerRvAdapter(context, donors.aNegativeDonors)
                }
            }
            "B+" -> {
                if (donors.bPositiveDonors.size == 0) {
                    holder.noUser.visibility = View.VISIBLE
                } else {
                    holder.innerRV.adapter = InnerRvAdapter(context, donors.bPositiveDonors)
                }
            }
            "B-" -> {
                if (donors.bNegativeDonors.size == 0) {
                    holder.noUser.visibility = View.VISIBLE
                } else {
                    holder.innerRV.adapter = InnerRvAdapter(context, donors.bNegativeDonors)
                }
            }
            "O+" -> {
                if (donors.oPositiveDonors.size == 0) {
                    holder.noUser.visibility = View.VISIBLE
                } else {
                    holder.innerRV.adapter = InnerRvAdapter(context, donors.oPositiveDonors)
                }
            }
            "O-" -> {
                if (donors.oNegativeDonors.size == 0) {
                    holder.noUser.visibility = View.VISIBLE
                } else {
                    holder.innerRV.adapter = InnerRvAdapter(context, donors.oNegativeDonors)
                }
            }
            "AB+" -> {
                if (donors.abPositiveDonors.size == 0) {
                    holder.noUser.visibility = View.VISIBLE
                } else {
                    holder.innerRV.adapter = InnerRvAdapter(context, donors.abPositiveDonors)
                }
            }
            "AB-" -> {
                if (donors.abNegativeDonors.size == 0) {
                    holder.noUser.visibility = View.VISIBLE
                } else {
                    holder.innerRV.adapter = InnerRvAdapter(context, donors.abNegativeDonors)
                }
            }
        }
    }

    override fun getItemCount(): Int = bloodTypeList.size
}