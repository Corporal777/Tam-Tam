package org.otunjargych.tamtam.ui.home

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.FragmentHomeBinding
import org.otunjargych.tamtam.ui.base.BaseFragment
import org.otunjargych.tamtam.ui.holders.PlaceholderItem
import org.otunjargych.tamtam.ui.home.items.NotesListItem
import org.otunjargych.tamtam.ui.home.items.CategoriesItem
import org.otunjargych.tamtam.ui.home.items.TitleItemHome
import org.otunjargych.tamtam.ui.interfaces.ToolbarFragment
import kotlin.reflect.KClass

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>(), ToolbarFragment {


    private val categoriesSection by lazy {
        Section().apply {
            setHeader(TitleItemHome(""))
            add(CategoriesItem())
        }
    }

    private val notesSection by lazy {
        Section().apply {
            setHeader(TitleItemHome(getString(R.string.actual_notes)))
            setHideWhenEmpty(true)
        }
    }
    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(categoriesSection)
            add(notesSection)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            homeList.adapter = groupAdapter
        }
        observeNotesList()

    }


    private fun observeNotesList() {
        viewModel.notesList.observe(viewLifecycleOwner) { list ->
            notesSection.update(listOf(NotesListItem(list)))
        }

    }

    private fun showChangeTown() {
        findNavController().navigate(R.id.town_fragment)
    }


    override val layoutId: Int = R.layout.fragment_home
    override fun getViewModelClass(): KClass<HomeViewModel> = HomeViewModel::class
    override val title: CharSequence by lazy { getString(R.string.home) }
}