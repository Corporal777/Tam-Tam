package org.otunjargych.tamtam.ui.my

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.FragmentMyNotesBinding
import org.otunjargych.tamtam.ui.base.BaseFragment
import org.otunjargych.tamtam.ui.interfaces.ToolbarFragment
import org.otunjargych.tamtam.ui.my.active.ActiveNotesFragment
import org.otunjargych.tamtam.ui.my.inactive.InActiveNotesFragment
import org.otunjargych.tamtam.util.onPageChanged
import kotlin.reflect.KClass

class MyNotesFragment : BaseFragment<MyNotesViewModel, FragmentMyNotesBinding>(true),
    ToolbarFragment {

    private var isFinishAnimation = true

    private val onScrollChange = object : OnScrollStateChange {
        override fun onScrollUp(value: Int) = showView(mBinding.lnTabsContainer)
        override fun onScrollDown(value: Int) = hideView(mBinding.lnTabsContainer)
        override fun onScrollOffsetValue(value: Float) {}
    }

    private val pageChangeListener = onPageChanged { viewModel.setTabPosition(it) }
    private val fragments by lazy {
        listOf(
            ActiveNotesFragment(onScrollChange),
            InActiveNotesFragment(onScrollChange)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.doOnPreDraw { startPostponedEnterTransition() }
        mBinding.apply {
            viewPager.run {
                isSaveEnabled = false
                addOnPageChangeListener(pageChangeListener)
                adapter = object : FragmentStatePagerAdapter(
                    childFragmentManager,
                    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
                ) {
                    override fun getItem(position: Int) = fragments[position] as Fragment

                    override fun getCount() = fragments.size
                }
                viewModel.setTabPosition(currentItem)
            }
            btnTabActive.setOnClickListener { viewPager.currentItem = 0 }
            btnTabInactive.setOnClickListener { viewPager.currentItem = 1 }
            btnTabChern.setOnClickListener { viewPager.currentItem = 2 }
        }
        observeTabPosition()
    }

    private fun observeTabPosition() {
        viewModel.tabPosition.observe(viewLifecycleOwner) { pos ->
            mBinding.lnTabs.apply {
                for (p in 0 until childCount) {
                    getChildAt(p).isSelected = p == pos
                }
            }
        }
    }

    private fun hideView(animationView: View?) {
        if (animationView == null || animationView.visibility == View.GONE) {
            return
        }
        val animationDown =
            AnimationUtils.loadAnimation(animationView.context, R.anim.move_up)
        animationDown.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                animationView.visibility = View.VISIBLE
                isFinishAnimation = false
            }

            override fun onAnimationEnd(animation: Animation) {
                animationView.visibility = View.GONE
                isFinishAnimation = true
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        if (isFinishAnimation) {
            animationView.startAnimation(animationDown)
        }
    }

    private fun showView(animationView: View?) {
        if (animationView == null || animationView.visibility == View.VISIBLE) {
            return
        }
        val animationUp =
            AnimationUtils.loadAnimation(animationView.context, R.anim.move_down)
        animationUp.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                animationView.visibility = View.VISIBLE
                isFinishAnimation = false
            }

            override fun onAnimationEnd(animation: Animation) {
                isFinishAnimation = true
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        if (isFinishAnimation) {
            animationView.startAnimation(animationUp)
        }
    }

    interface OnScrollStateChange {
        fun onScrollUp(value: Int)
        fun onScrollDown(value: Int)
        fun onScrollOffsetValue(value: Float)
    }

    override val layoutId = R.layout.fragment_my_notes
    override fun getViewModelClass(): KClass<MyNotesViewModel> = MyNotesViewModel::class
    override val title: CharSequence by lazy { getString(R.string.my_notes) }
    override fun toolbarIconsContainer(viewGroup: ViewGroup) {

    }
}