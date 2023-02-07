package org.otunjargych.tamtam.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ActivityMainBinding
import org.otunjargych.tamtam.ui.auth.login.LoginFragment
import org.otunjargych.tamtam.ui.auth.register.RegisterFragment
import org.otunjargych.tamtam.ui.createNote.CreateNoteFragment
import org.otunjargych.tamtam.ui.home.HomeFragment
import org.otunjargych.tamtam.ui.interfaces.ToolbarFragment
import org.otunjargych.tamtam.ui.profile.ProfileFragment
import org.otunjargych.tamtam.ui.profileSettings.ProfileSettingsFragment
import org.otunjargych.tamtam.ui.town.TownFragment
import org.otunjargych.tamtam.ui.views.CustomSnackBar
import org.otunjargych.tamtam.ui.views.LoadingProgressDialog
import org.otunjargych.tamtam.util.getFragmentLifecycleCallback


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by lazy { getViewModel(null, MainViewModel::class) }
    private val mLoadingDialog by lazy { LoadingProgressDialog(this) }

    private val fragmentsLifecycleCallback = getFragmentLifecycleCallback(
        onViewCreated = { f ->
            setupNavBarItems(f)
            when (f) {
                is LoginFragment,
                is RegisterFragment,
                is TownFragment,
                is CreateNoteFragment,
                is ProfileSettingsFragment -> hideNavBar()
                else -> showNavBar()
            }
            if (f is ToolbarFragment) {
                binding.apply {
                    mainAppBar.isVisible = true
                    authToolbar.customTitle.text = f.title
                }
            } else {
                binding.mainAppBar.isVisible = false
            }
        }, onViewDestroyed = {}
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerFragmentLifecycleCallback()
        setupMainNavBar()
        observeUserTown()
    }


    private fun observeUserTown() {
        viewModel.userTown.observe(this) {
            if (it.isNullOrEmpty()) showChangeTown()
        }
    }

    private fun setupMainNavBar() {
        //binding.mainNavBar.background = null
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        binding.navInclude.mainBottomNavView.setupWithNavController(navHost.navController)
        binding.navInclude.mainBottomNavView.setOnItemReselectedListener { item ->
        }
        binding.navInclude.mainBottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.main -> {
                    if (!findNavController().popBackStack(R.id.home_fragment, false)) {
                        findNavController().navigate(R.id.home_fragment, null,
                            navOptions {
                                popUpTo(R.id.nav_graph) { inclusive = true }
                            })
                    }
                    true
                }
                R.id.profile -> {
                    findNavController().navigate(R.id.profile_fragment)
                    true
                }
                else -> false
            }
        }
        binding.navInclude.ivNewNote.setOnClickListener {
            showCreateNote()
        }
    }

    private fun findNavController() = findNavController(R.id.fragmentContainerView)

    private fun showChangeTown() {
        findNavController().navigate(R.id.town_fragment, null,
            navOptions {
                popUpTo(R.id.nav_graph) { inclusive = true }
            })
    }

    private fun showCreateNote(){
        findNavController().navigate(R.id.create_note_fragment)
    }

    private fun setupNavBarItems(f: Fragment) {
        when (f) {
            is HomeFragment -> binding.navInclude.mainBottomNavView.menu.findItem(R.id.main).isChecked = true
            is ProfileFragment -> binding.navInclude.mainBottomNavView.menu.findItem(R.id.profile).isChecked =
                true
        }
    }

    override fun onDestroy() {
        unregisterFragmentLifecycleCallback()
        super.onDestroy()
    }

    private fun registerFragmentLifecycleCallback() {
        mainActivity = this
        val fr =
            (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment)
        fr.childFragmentManager.registerFragmentLifecycleCallbacks(
            fragmentsLifecycleCallback,
            false
        )
    }

    private fun unregisterFragmentLifecycleCallback() {
        mainActivity = null
        val fr =
            (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment)
        fr.childFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentsLifecycleCallback)
    }

    fun showSnackBar(message: String, icon: Int) {
        val snack = CustomSnackBar.make(binding.root, Snackbar.LENGTH_LONG)
        //snack.anchorView = binding.mainBottomNavView
        snack.setText(message)
        snack.setIcon(icon)
        snack.show()
    }

    fun showLoadingDialog() {
        mLoadingDialog.showLoading()
    }

    fun hideLoadingDialog() {
        mLoadingDialog.hideLoading()
    }

    fun showLoadingView() {
        binding.progressView.isVisible = true
        binding.progressView.playAnimation()
    }

    fun hideLoadingView() {
        binding.progressView.isVisible = false
        binding.progressView.pauseAnimation()
    }

    private fun hideNavBar() {
        binding.navInclude.navBarContainer.isVisible = false
        //binding.fab.hide()
       // binding.mainNavBar.performHide(true)
    }


    private fun showNavBar() {
       // binding.fab.show()
    binding.navInclude.navBarContainer.isVisible = true
    }

    companion object {
        private var mainActivity: MainActivity? = null
        fun getInstance() = this.mainActivity
    }
}
