package org.otunjargych.tamtam.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.google.firebase.auth.FirebaseAuth
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ActivityMainBinding
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.fragments.*
import org.otunjargych.tamtam.fragments.dialog_fragments.MyDialogFragment


class MainActivity : AppCompatActivity(), MyDialogFragment.OnFragmentSendDataListener,
    OnBottomAppBarStateChangeListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AUTH = FirebaseAuth.getInstance()

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                setReorderingAllowed(true)
                add(R.id.fragment_container, MainFragment())
            }
        }

        setOnMenuItem()
        setBottomAppBarAndFab()
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

    private fun setBottomAppBarAndFab() {
        binding.fab.apply {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
            setOnClickListener {
                if (AUTH.currentUser != null) {
                    replaceFragment(AdFragment())
                } else {
                    startActivity(Intent(this@MainActivity, RegistrationActivity::class.java))
                    finish()
                }
            }
        }
    }


    private fun hideBottomAppBar() {
        binding.fab.hide()
        binding.run {
            bottomAppBar.performHide()
            bottomAppBar.animate().setListener(object : AnimatorListenerAdapter() {
                var isCanceled = false
                override fun onAnimationEnd(animation: Animator?) {
                    if (isCanceled) return

                    bottomAppBar.visibility = View.GONE
                    fab.visibility = View.INVISIBLE
                }

                override fun onAnimationCancel(animation: Animator?) {
                    isCanceled = true
                }
            })
        }

    }

    private fun showBottomAppBar() {
        binding.run {
            if (!fab.isShown) {
                fab.show()
            }
            bottomAppBar.visibility = View.VISIBLE
            bottomAppBar.performShow()

        }
    }


    private fun setOnMenuItem() {

        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
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

    override fun onHide() {
        hideBottomAppBar()
    }

    override fun onShow() {
        showBottomAppBar()
    }

}