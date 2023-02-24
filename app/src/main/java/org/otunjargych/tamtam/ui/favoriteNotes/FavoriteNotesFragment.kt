package org.otunjargych.tamtam.ui.favoriteNotes

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.CompoundButton
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.FragmentFavoriteNotesBinding
import org.otunjargych.tamtam.ui.base.BaseFragment
import org.otunjargych.tamtam.ui.holders.NoteEmptyItem
import org.otunjargych.tamtam.ui.holders.OnNoteActionsListener
import org.otunjargych.tamtam.ui.home.items.NotesListItem
import org.otunjargych.tamtam.ui.home.items.TitleItemHome
import org.otunjargych.tamtam.ui.interfaces.ToolbarFragment
import org.otunjargych.tamtam.ui.views.FilterButtonView
import org.otunjargych.tamtam.util.findItemBy
import org.otunjargych.tamtam.util.onScrollStateChanged
import org.otunjargych.tamtam.util.onScrolled
import org.otunjargych.tamtam.util.updateItem
import kotlin.reflect.KClass


class FavoriteNotesFragment : BaseFragment<FavoriteNotesViewModel, FragmentFavoriteNotesBinding>(),
    ToolbarFragment {

    private var filtersAdded = false

    private val notesSection by lazy { Section() }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFilters()
        mBinding.apply {
            listNotes.apply {
                adapter = groupAdapter
                onScrolled { dx, dy ->
                    if (dy > 20) hideToolbarFilters()
                    else if (dy < -5) showToolbarFilters()
                }
            }
            observeNotesList()
            observeEmptyListPlaceholder()
        }
    }

    private fun observeNotesList() {
        viewModel.notesList.observe(viewLifecycleOwner) { list ->
            notesSection.apply {
                setHeader(TitleItemHome(getString(R.string.favorite_notes_tite)))
                val item = findItemBy<NotesListItem> { true }
                if (item == null) updateItem(NotesListItem(list, onNoteClickListener))
                else item.updateItems(list)
            }
        }
    }

    private fun observeEmptyListPlaceholder() {
        viewModel.placeholder.observe(viewLifecycleOwner) {
            notesSection.apply {
                removeHeader()
                updateItem(NoteEmptyItem(it))
            }
        }
    }


    private fun initFilters() {
        if (!filtersAdded) {
            val createChip: (FavoriteFilter) -> CompoundButton = { filter ->
                FilterButtonView(requireContext()).apply {
                    id = filter.id
                    text = filter.name
                    isChecked = filter.isChecked

                    setOnCheckedChangeListener { _, isChecked ->
                        filter.isChecked = isChecked
                        viewModel.onFilterChanged(filter)
                    }
                }
            }
            mBinding.include.lnFiltersContainer.apply {
                removeAllViews()
                viewModel.getFavoriteFilters().forEach { s -> addView(createChip(s)) }
                filtersAdded = true
            }
        }
    }

    private fun hideToolbarFilters() {
        mBinding.favoriteAppBar.animate()
            .translationY((-mBinding.include.filtersToolbar.bottom).toFloat())
            .setInterpolator(AccelerateInterpolator()).start()
    }

    private fun showToolbarFilters() {
        mBinding.favoriteAppBar.animate().translationY(0f)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    override val layoutId = R.layout.fragment_favorite_notes
    override fun getViewModelClass(): KClass<FavoriteNotesViewModel> = FavoriteNotesViewModel::class

    override val title: CharSequence by lazy { getString(R.string.favorite_notes) }
    override fun toolbarIconsContainer(viewGroup: ViewGroup) {

    }

}