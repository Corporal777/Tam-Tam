package org.otunjargych.tamtam.ui.createNote

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.FragmentCreateNoteBinding
import org.otunjargych.tamtam.databinding.FragmentHomeBinding
import org.otunjargych.tamtam.ui.base.BaseFragment
import org.otunjargych.tamtam.ui.createNote.items.AddressDataItem
import org.otunjargych.tamtam.ui.createNote.items.MainDataItem
import org.otunjargych.tamtam.ui.createNote.items.TitleDataItem
import org.otunjargych.tamtam.ui.createNote.items.WorkAdditionalDataItem
import org.otunjargych.tamtam.ui.home.HomeViewModel
import org.otunjargych.tamtam.ui.home.items.CategoriesItem
import org.otunjargych.tamtam.ui.home.items.NotesListItem
import org.otunjargych.tamtam.ui.home.items.TitleItemHome
import org.otunjargych.tamtam.ui.interfaces.ToolbarFragment
import kotlin.reflect.KClass

class CreateNoteFragment : BaseFragment<CreateNoteViewModel, FragmentCreateNoteBinding>(),
    ToolbarFragment {


    private val mainDataSection by lazy {
        Section().apply {
            setHeader(TitleDataItem(getString(R.string.main_information)))
        }
    }

    private val addressDataSection by lazy {
        Section().apply {
            setHeader(TitleDataItem(getString(R.string.address_title)))

        }
    }
    private val additionalDataSection by lazy { Section() }

    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(mainDataSection)
            add(addressDataSection)
            add(additionalDataSection)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            createNoteList.adapter = groupAdapter
        }
        initSections()
    }

    private fun initSections() {
        mainDataSection.apply {
            update(listOf(MainDataItem().apply {
                onCategoryChangeCallback = {
                    if (!it.isNullOrEmpty()) {
                        additionalDataSection.update(listOf(WorkAdditionalDataItem()))
                    }
                }
            }))
        }
        addressDataSection.apply {
            update(listOf(AddressDataItem(requireActivity(), viewModel.getCurrentTown())))
        }
    }


    override val layoutId: Int = R.layout.fragment_create_note
    override fun getViewModelClass(): KClass<CreateNoteViewModel> = CreateNoteViewModel::class
    override val title: CharSequence by lazy { getString(R.string.new_note) }
}