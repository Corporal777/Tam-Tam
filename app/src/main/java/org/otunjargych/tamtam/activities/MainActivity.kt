package org.otunjargych.tamtam.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ActivityMainBinding
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.fragments.*
import org.otunjargych.tamtam.fragments.dialog_fragments.MyDialogFragment
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MyDialogFragment.OnFragmentSendDataListener,
    OnBottomAppBarStateChangeListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFields()

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.fragment_container, MainFragment())
            }
        }

        setOnMenuItem()
        setBottomAppBarAndFab()
    }

    override fun onSendData(data: String?) {

    }

    private fun setBottomAppBarAndFab() {
        binding.fab.apply {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
            setOnClickListener {
                if (AUTH.currentUser != null) {
                    replaceFragment(NewAdFragment())
                } else {
                    val intent = Intent(this@MainActivity, RegistrationActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right
                    )

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
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace(R.id.fragment_container, MainFragment())
                    }
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
                R.id.settings -> {
                    replaceFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun clearBackStack() {
        val manager: FragmentManager = supportFragmentManager
        if (manager.backStackEntryCount > 0) {
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

    private fun initFields() {
        REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
        REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
        AUTH = FirebaseAuth.getInstance()
        if (AUTH.currentUser != null) {
            USER_ID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        }
    }

}