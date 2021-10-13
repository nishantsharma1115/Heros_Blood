package com.nishant.herosblood.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nishant.herosblood.adapters.OurTeamAdapter
import com.nishant.herosblood.databinding.ActivityOurTeamBinding
import com.nishant.herosblood.models.Member

class OurTeamActivity : AppCompatActivity() {

    private var _binding: ActivityOurTeamBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAdapter: OurTeamAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOurTeamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecycler()

        binding.imgBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecycler() {
        mAdapter = OurTeamAdapter(
            membersData()
        ) {
            when (it.second) {
                "facebook" -> openFacebookIntent(it.first)
                "instagram" -> openInstagramIntent(it.first)
                "linkedin" -> openLinkedinIntent(it.first)
                "github" -> openGithubIntent(it.first)
            }
        }
        binding.rvAbout.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@OurTeamActivity)
        }
        mAdapter.notifyDataSetChanged()
    }

    private fun openGithubIntent(url: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            )
        )
    }
    private fun openLinkedinIntent(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            intent.setPackage("com.linkedin.android")
            startActivity(intent)
        } catch (anfe: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(url)
                )
            )
        }
    }

    private fun openFacebookIntent(userName: String) {
        try {
            if (isAppInstalled("com.facebook.orca") ||
                isAppInstalled("com.facebook.katana") ||
                isAppInstalled("com.example.facebook") ||
                isAppInstalled("com.facebook.android")
            ) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(userName)))
            } else {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(userName)))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            this.packageManager.getApplicationInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun openInstagramIntent(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            intent.setPackage("com.instagram.android")
            startActivity(intent)
        } catch (anfe: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(url)
                )
            )
        }
    }

    private fun membersData(): List<Member> {
        val membersList = mutableListOf<Member>()
        membersList.add(
            Member(
                "Nishant Sharma",
                "Founder | Android Developer",
                "https://avatars.githubusercontent.com/u/64049931?v=4",
                "https://www.facebook.com/nishant1115/",
                "https://www.instagram.com/_ni5hant_/",
                "https://www.linkedin.com/in/nishantsharma1115/",
                "https://github.com/nishantsharma1115"
            )
        )
        membersList.add(
            Member(
                "Vaibhav Jaiswal",
                "Android Developer",
                "https://avatars.githubusercontent.com/u/64367926?v=4",
                "https://www.facebook.com/vaibhav.jaiswal.902604",
                "https://www.instagram.com/vaibhav.jaiswal.2511/",
                "https://www.linkedin.com/in/vaibhav-jaiswal-8612621b7",
                "https://github.com/Vaibhav2002"
            )
        )
        membersList.add(
            Member(
                "Nishant Kumar",
                "Android Developer",
                "https://avatars.githubusercontent.com/u/64367722?s=400&u=a8fdb98cfb4fdf161a0f8f59670cde01b1abf0e1&v=4",
                "https://www.facebook.com/knishant362/",
                "https://www.instagram.com/_.trendster._/",
                "https://www.linkedin.com/in/nishantkumar26/",
                "https://github.com/knishant362"
            )
        )
        membersList.add(
            Member(
                "Rohan Singh Slathia",
                "Illustrator",
                "https://avatars.githubusercontent.com/u/75566120?v=4",
                "https://m.facebook.com/rohan.slathia.3990",
                "https://www.instagram.com/rohan_slathia",
                "https://www.linkedin.com/in/rohan-singh-slathia-027430190",
                "https://github.com/Rohanslathia"
            )
        )

        return membersList
    }
}
