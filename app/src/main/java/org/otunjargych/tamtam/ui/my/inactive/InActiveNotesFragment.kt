package org.otunjargych.tamtam.ui.my.inactive

import android.os.Bundle
import android.view.View
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.FragmentMyNotesListBinding
import org.otunjargych.tamtam.ui.base.BaseFragment
import org.otunjargych.tamtam.ui.holders.OnNoteActionsListener
import org.otunjargych.tamtam.ui.home.items.NotesListItem
import org.otunjargych.tamtam.ui.home.items.TitleItemHome
import org.otunjargych.tamtam.ui.my.MyNotesFragment
import org.otunjargych.tamtam.ui.my.MyNotesViewModel
import org.otunjargych.tamtam.util.onScrolled
import org.otunjargych.tamtam.util.updateItem
import kotlin.reflect.KClass

class InActiveNotesFragment (private val onScrollState: MyNotesFragment.OnScrollStateChange) :
    BaseFragment<MyNotesViewModel, FragmentMyNotesListBinding>() {


    private val notesSection by lazy {
        Section().apply {
            setHeader(TitleItemHome("Объявления на модерации"))
            setHideWhenEmpty(true)
        }
    }

    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(notesSection)
        }
    }

    private val onNoteClickListener = object : OnNoteActionsListener {
        override fun onNoteClick(noteId: String) {

        }

        override fun onNoteLikeClick(noteId: String) {
        }

        override fun onNoteDislikeClick(noteId: String) {
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getInactiveNotes()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            myNotesList.apply {
                adapter = groupAdapter
                onScrolled { _, dy ->
                    if (this.computeVerticalScrollOffset() <= 10) {
                        onScrollState.onScrollOffsetValue(this.computeVerticalScrollOffset().toFloat())
                    } else onScrollState.onScrollOffsetValue(10f)

                    if (dy <= 0) {
                        onScrollState.onScrollUp(dy)
                    } else {
                        onScrollState.onScrollDown(dy)
                    }
                }
            }
        }
        observeNotesList()
    }

    private fun observeNotesList() {
        viewModel.inActiveNotes.observe(viewLifecycleOwner) { list ->
            notesSection.updateItem(NotesListItem(list, onNoteClickListener))
        }
    }

    override val layoutId = R.layout.fragment_my_notes_list
    override fun getViewModelClass(): KClass<MyNotesViewModel> = MyNotesViewModel::class

}