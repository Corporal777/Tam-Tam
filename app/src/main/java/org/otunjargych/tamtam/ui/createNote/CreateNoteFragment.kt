package org.otunjargych.tamtam.ui.createNote

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.FragmentCreateNoteBinding
import org.otunjargych.tamtam.ui.base.BaseFragment
import org.otunjargych.tamtam.ui.createNote.items.*
import org.otunjargych.tamtam.ui.holders.MainButtonItem
import org.otunjargych.tamtam.ui.interfaces.ToolbarFragment
import org.otunjargych.tamtam.ui.views.dialogs.NoteCreatedBottomSheetDialog
import org.otunjargych.tamtam.util.*
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
    private val additionalDataSection by lazy {
        Section().apply {
            setHeader(TitleDataItem(getString(R.string.additional_information)))
            setHideWhenEmpty(true)
        }
    }

    private val imagesSection by lazy {
        Section().apply {
            setHeader(TitleDataItem(getString(R.string.photos_title)))
        }
    }

    private val saveButtonSection by lazy {
        Section().apply {
            add(MainButtonItem(true, getString(R.string.publicate)) {
                createNote()
            })
        }
    }

    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(mainDataSection)
            add(addressDataSection)
            //add(additionalDataSection)
            add(imagesSection)
            add(saveButtonSection)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            createNoteList.adapter = groupAdapter
        }
        initSections()
        observeSelectedImages()
        observeNoteSuccessCreated()
    }

    private fun initSections() {
        mainDataSection.apply {
            updateGroup(MainDataGroup(requireContext()))
        }
        addressDataSection.apply {
            updateItem(AddressDataItem(requireActivity(), viewModel.getCurrentTown()))
        }

        imagesSection.apply {
            updateItem(
                ImagesListItem(null,
                    { viewModel.showGallery() },
                    { viewModel.removeSelectedImage(it) }
                )
            )
        }


    }

    private fun createNote() {
        val mainDataItem = mainDataSection.findGroupBy<MainDataGroup> { true }
        val addressDataItem = addressDataSection.findItemBy<AddressDataItem> { true }

        if (mainDataItem != null && addressDataItem != null) {
            if (mainDataItem.isMainDataValid() && addressDataItem.isAddressDataValid()) {
                if (mainDataItem.isAdditionalDataValid()) {
                    viewModel.createNote(
                        mainDataItem.getMainData(),
                        addressDataItem.getAddressData(),
                        mainDataItem.getContactsData(),
                        mainDataItem.getAdditionalData()
                    )
                }
            }
        }
    }

    private fun observeSelectedImages() {
        viewModel.selectedImages.observe(viewLifecycleOwner) {
            imagesSection.findItemBy<ImagesListItem> { true }?.update(it)
        }
    }

    private fun observeNoteSuccessCreated() {
        viewModel.noteCreated.observe(viewLifecycleOwner) {
            if (it) {
                NoteCreatedBottomSheetDialog(requireContext()).setActionCallback {
                    showHome()
                }
            }
        }
    }

    private fun showHome() {
        findNavController().navigate(R.id.home_fragment, null,
            navOptions {
                popUpTo(R.id.nav_graph) { inclusive = true }
            })
    }


    override val layoutId: Int = R.layout.fragment_create_note
    override fun getViewModelClass(): KClass<CreateNoteViewModel> = CreateNoteViewModel::class
    override val title: CharSequence by lazy { getString(R.string.new_note) }
    override fun toolbarIconsContainer(viewGroup: ViewGroup) {}
}