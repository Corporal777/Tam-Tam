package org.otunjargych.tamtam.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ActivityMainBinding
import org.otunjargych.tamtam.extensions.USER
import org.otunjargych.tamtam.extensions.hasConnection
import org.otunjargych.tamtam.extensions.replaceFragment
import org.otunjargych.tamtam.fragments.*
import org.otunjargych.tamtam.fragments.dialog_fragments.CategoryDialogFragment
import org.otunjargych.tamtam.fragments.dialog_fragments.MyDialogFragment


class MainActivity : AppCompatActivity(), MyDialogFragment.OnFragmentSendDataListener,
    CategoryDialogFragment.OnFragmentSendCategoryListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                setReorderingAllowed(true)
                add(R.id.fragment_container, MainFragment())
            }
        }

        setOnMenuItem()

    }

    override fun onSendData(data: String?) {
        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (fragment is AdFragment) {
            fragment.setSelectedItem(data)
        }
//        if (fragment is WorksFragment) {
//            fragment.setSelectedStation(data)
//        }
        if (fragment is TransportFragment) {
            fragment.setSelectedStation(data)
        }
        if (fragment is BeautyFragment) {
            fragment.setSelectedStation(data)
        }
        if (fragment is BuySellFragment) {
            fragment.setSelectedStation(data)
        }
        if (fragment is FlatsFragment) {
            fragment.setSelectedStation(data)
        }
        if (fragment is ServicesFragment) {
            fragment.setSelectedStation(data)
        }
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }


    override fun onSendCategory(category: String?) {
        val fragment: AdFragment? = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as AdFragment?
        if (fragment != null) fragment.setSelectedCategory(category)
    }


    private fun setOnMenuItem() {
        binding.bottomBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.main -> {

                    replaceFragment(MainFragment())
                    clearBackStack()
                    true
                }
                R.id.liked_ads -> {
                    if (hasConnection(applicationContext) && USER.currentUser != null) {
                        replaceFragment(LikedAdsFragment())
                        true
                    } else
                        false
                }
                R.id.about_app -> {
                    replaceFragment(AboutAppFragment())
                    true
                }
                R.id.own_acc -> {
                    if (USER.currentUser != null) {
                        replaceFragment(AccountFragment())
                        true
                    } else
                        false
                }
                R.id.net_with_dev -> {
                    val url: String =
                        "https://api.whatsapp.com/send?phone=" + "+79267806176"
                    val i: Intent = Intent(Intent.ACTION_VIEW)
                    i.setData(Uri.parse(url))
                    startActivity(i)
                    true
                }
                else -> false
            }
        }
    }

    private fun clearBackStack() {

        val manager: FragmentManager = supportFragmentManager
        if (manager.getBackStackEntryCount() > 0) {
            val first: FragmentManager.BackStackEntry = manager.getBackStackEntryAt(0)
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }
}