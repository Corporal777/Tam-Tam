package org.otunjargych.tamtam.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ActivityMainBinding
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.fragments.LikedNodesFragment
import org.otunjargych.tamtam.fragments.MainFragment
import org.otunjargych.tamtam.fragments.NewNodeFragment
import org.otunjargych.tamtam.fragments.SettingsFragment


class MainActivity : BaseActivity(), OnBottomAppBarStateChangeListener,
    OnBottomAppBarItemsEnabledListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mAppUpdateManager: AppUpdateManager
    private val UPDATE_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this)

        initUpdateManager()
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

    private fun setBottomAppBarAndFab() {
        binding.fab.apply {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
            setOnClickListener {
                if (hasConnection(this@MainActivity)) {
                    if (AUTH.currentUser != null) {
                        replaceFragment(NewNodeFragment())
                    } else {
                        val intent = Intent(this@MainActivity, RegistrationActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    toastMessage(this@MainActivity, getString(R.string.no_connection))
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
                    replaceFragment(1)
                    clearBackStack()
                    true
                }
                R.id.liked_nodes -> {
                    if (hasConnection(this)) {
                        if (AUTH.currentUser != null) {
                            replaceFragment(2)
                            true
                        } else {
                            val intent = Intent(this@MainActivity, RegistrationActivity::class.java)
                            startActivity(intent)
                            true
                        }
                    } else {
                        toastMessage(this, getString(R.string.no_connection))
                        true
                    }
                }
                R.id.settings -> {
                    replaceFragment(3)
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
        FF_DATABASE_ROOT = Firebase.firestore
        REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
        REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
        AUTH = FirebaseAuth.getInstance()
        if (AUTH.currentUser != null) {
            USER_ID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        }
    }

    private fun replaceFragment(status: Int) {
        var fragment: Fragment = MainFragment()
        var tag = ""
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            when (status) {
                1 -> {
                    replace(R.id.fragment_container, fragment)
                }
                2 -> {
                    tag = "liked_nodes"
                    fragment = LikedNodesFragment()
                    replace(R.id.fragment_container, fragment)
                    addToBackStack(tag)
                }
                3 -> {
                    tag = "settings"
                    fragment = SettingsFragment()
                    replace(R.id.fragment_container, fragment)
                    addToBackStack(tag)
                }
            }
        }
    }

    override fun enabledHomeItem() {
        binding.bottomAppBar.menu.findItem(R.id.main).isEnabled = true
        binding.bottomAppBar.menu.findItem(R.id.main).setIcon(R.drawable.ic_home)
    }

    override fun enabledSettingsItem() {
        binding.bottomAppBar.menu.findItem(R.id.settings).isEnabled = true
        binding.bottomAppBar.menu.findItem(R.id.settings).setIcon(R.drawable.ic_settings)
    }

    override fun enabledLikedItem() {
        binding.bottomAppBar.menu.findItem(R.id.liked_nodes).isEnabled = true
        binding.bottomAppBar.menu.findItem(R.id.liked_nodes).setIcon(R.drawable.ic_liked_ads)
    }

    override fun disabledHomeItem() {
        binding.bottomAppBar.menu.findItem(R.id.main).isEnabled = false
        binding.bottomAppBar.menu.findItem(R.id.main).setIcon(R.drawable.ic_home_colored)
    }


    override fun disabledSettingsItem() {
        binding.bottomAppBar.menu.findItem(R.id.settings).isEnabled = false
        binding.bottomAppBar.menu.findItem(R.id.settings).setIcon(R.drawable.ic_settings_colored)
    }

    override fun disabledLikedItem() {
        binding.bottomAppBar.menu.findItem(R.id.liked_nodes).isEnabled = false
        binding.bottomAppBar.menu.findItem(R.id.liked_nodes).setIcon(R.drawable.ic_liked_colored)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == UPDATE_CODE) {
            if (resultCode != RESULT_OK) {
                Log.d("Error", "error")
                toastMessage(this, "Обновление отклонено!")
            }
        }

        super.onActivityResult(requestCode, resultCode, data)

    }


    private fun initUpdateManager() {
        mAppUpdateManager = AppUpdateManagerFactory.create(this)
        mAppUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && it.isUpdateTypeAllowed(
                    AppUpdateType.FLEXIBLE
                )
            ) {
                if (!this.isFinishing) {
                    try {
                        mAppUpdateManager.startUpdateFlowForResult(
                            it,
                            AppUpdateType.FLEXIBLE,
                            this,
                            UPDATE_CODE
                        )
                    } catch (e: Exception) {
                        Log.e("Error", "SendError: $e");
                    }
                }
            }
        }
        mAppUpdateManager.registerListener(onRegisterListener())
    }

    private fun onRegisterListener(): InstallStateUpdatedListener {
        val listener = InstallStateUpdatedListener {
            if (it.installStatus() == InstallStatus.DOWNLOADED) {
                showUpdateMessage()
            }
        }
        return listener
    }

    private fun showUpdateMessage() {
        Snackbar.make(
            findViewById(android.R.id.content),
            "Обновление готово",
            Snackbar.LENGTH_INDEFINITE
        ).also { snackbar ->
            snackbar.setBackgroundTint(this.resources.getColor(R.color.app_main_color))
            snackbar.setActionTextColor(this.resources.getColor(R.color.white))
            val textview =
                snackbar.view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
            textview.textSize = 16F
            val font = Typeface.createFromAsset(this.assets, "commons_medium.ttf")
            textview.typeface = font
            snackbar.setAction("Установка") {
                mAppUpdateManager.completeUpdate()
            }
        }.show()
    }


    override fun onStop() {
        if (mAppUpdateManager != null) {
            mAppUpdateManager.unregisterListener(onRegisterListener())
        }
        super.onStop()

    }
}
