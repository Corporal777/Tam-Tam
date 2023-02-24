package org.otunjargych.tamtam.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ActivityMainBinding
import org.otunjargych.tamtam.ui.auth.login.LoginFragment
import org.otunjargych.tamtam.ui.auth.register.RegisterFragment
import org.otunjargych.tamtam.ui.base.BaseActivity
import org.otunjargych.tamtam.ui.createNote.CreateNoteFragment
import org.otunjargych.tamtam.ui.detailNote.NoteDetailFragment
import org.otunjargych.tamtam.ui.favoriteNotes.FavoriteNotesFragment
import org.otunjargych.tamtam.ui.home.HomeFragment
import org.otunjargych.tamtam.ui.interfaces.ToolbarFragment
import org.otunjargych.tamtam.ui.my.MyNotesFragment
import org.otunjargych.tamtam.ui.profile.ProfileFragment
import org.otunjargych.tamtam.ui.profileSettings.ProfileSettingsFragment
import org.otunjargych.tamtam.ui.stories.StoriesFragment
import org.otunjargych.tamtam.ui.town.TownFragment
import org.otunjargych.tamtam.ui.views.CustomSnackBar
import org.otunjargych.tamtam.ui.views.LoadingProgressDialog
import org.otunjargych.tamtam.util.cancelWindowTransparency
import org.otunjargych.tamtam.util.doEdgeWindow
import org.otunjargych.tamtam.util.getFragmentLifecycleCallback
import org.otunjargych.tamtam.util.setWindowTransparency


class MainActivity : BaseActivity() {

    private val mLoadingDialog by lazy { LoadingProgressDialog(this) }

    private val fragmentsLifecycleCallback = getFragmentLifecycleCallback(
        onViewCreated = { f ->
            setupNavBarItems(f)
            when (f) {
                is LoginFragment,
                is RegisterFragment,
                is TownFragment,
                is CreateNoteFragment,
                is StoriesFragment,
                is MyNotesFragment,
                is NoteDetailFragment,
                is ProfileSettingsFragment -> hideNavBar()
                else -> showNavBar()
            }
            if (f is ToolbarFragment) {
                binding.apply {
                    mainAppBar.isVisible = true
                    authToolbar.customTitle.text = f.title
                    f.toolbarIconsContainer(authToolbar.iconsContainer.apply { removeAllViews() })
                }
            } else {
                binding.mainAppBar.isVisible = false
            }
        },
        onViewDestroyed = {},
        onFragmentStarted = { f ->
            when (f) {
                is StoriesFragment -> doEdgeWindow()
                is NoteDetailFragment -> setWindowTransparency()
                else -> cancelWindowTransparency()
            }
        },
        onFragmentStopped = { f ->

        }
    )

    private val backClick = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val navHost =
                supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
            val fr = navHost.childFragmentManager.fragments[0]

            if (fr is ProfileFragment || fr is FavoriteNotesFragment) {
                if (!findNavController().popBackStack(R.id.home_fragment, false)) showHome()
            } else if (fr is HomeFragment) {
                finish()
            } else {
                findNavController().navigateUp()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this, backClick)

        registerFragmentLifecycleCallback()
        setupMainNavBar()
        observeUserTown()
        observeErrorMessage()
    }


    private fun observeUserTown() {
        viewModel.userTown.observe(this) {
            if (!it.isNullOrEmpty()) showHome()
            else showChangeTown()
        }
    }

    private fun observeErrorMessage() {
        viewModel.errorMessage.observe(this) {
            showToast(it)
        }
    }

    private fun setupMainNavBar() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        binding.navInclude.mainBottomNavView.setupWithNavController(navHost.navController)
        binding.navInclude.mainBottomNavView.setOnItemReselectedListener { item ->
        }
        binding.navInclude.mainBottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.main -> {
                    if (!findNavController().popBackStack(R.id.home_fragment, false)) {
                        showHome()
                    }
                    true
                }
                R.id.profile -> {
                    findNavController().navigate(R.id.profile_fragment)
                    true
                }
                R.id.favorites -> {
                    findNavController().navigate(R.id.favorite_notes_fragment)
                    true
                }
                else -> false
            }
        }
        binding.navInclude.ivNewNote.setOnClickListener {
            if (viewModel.isUserAuthorized()) showCreateNote()
            else showLogin()
        }
    }

    private fun findNavController() = findNavController(R.id.fragmentContainerView)

    private fun showChangeTown() {
        findNavController().navigate(R.id.town_fragment, null,
            navOptions {
                popUpTo(R.id.nav_graph) { inclusive = true }
            })
    }

    private fun showHome() {
        findNavController().navigate(R.id.home_fragment, null,
            navOptions {
                popUpTo(R.id.nav_graph) { inclusive = true }
            })
    }


    private fun showCreateNote() {
        findNavController().navigate(R.id.create_note_fragment)
    }

    private fun showLogin() {
        findNavController().navigate(R.id.login_fragment, bundleOf("fromNote" to true))
    }

    private fun setupNavBarItems(f: Fragment) {
        when (f) {
            is HomeFragment -> binding.navInclude.mainBottomNavView.menu.findItem(R.id.main).isChecked =
                true
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

    fun getMainAppBar() = binding.mainAppBar

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
