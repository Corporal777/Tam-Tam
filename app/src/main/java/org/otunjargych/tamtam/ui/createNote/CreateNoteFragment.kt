package org.otunjargych.tamtam.ui.createNote

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.FragmentCreateNoteBinding
import org.otunjargych.tamtam.model.request.NoteDraftModel
import org.otunjargych.tamtam.ui.base.BaseFragment
import org.otunjargych.tamtam.ui.createNote.items.*
import org.otunjargych.tamtam.ui.holders.MainButtonItem
import org.otunjargych.tamtam.ui.interfaces.ToolbarFragment
import org.otunjargych.tamtam.ui.views.dialogs.ActionsMessageDialog
import org.otunjargych.tamtam.ui.views.dialogs.NoteCreatedBottomSheetDialog
import org.otunjargych.tamtam.util.*
import kotlin.reflect.KClass

class CreateNoteFragment : BaseFragment<CreateNoteViewModel, FragmentCreateNoteBinding>(),
    ToolbarFragment {


    private val mainDataSection by lazy {
        Section().apply {
            setHeader(TitleDataItem(getString(R.string.main_information)))
            setHideWhenEmpty(true)
        }
    }

    private val addressDataSection by lazy {
        Section().apply {
            setHeader(TitleDataItem(getString(R.string.address_title)))
            setHideWhenEmpty(true)
        }
    }

    private val imagesSection by lazy {
        Section().apply {
            setHeader(TitleDataItem(getString(R.string.photos_title)))
            setHideWhenEmpty(true)
        }
    }

    private val saveButtonSection by lazy { Section() }

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
        onBackPressedCallback(true) {
            showSaveDraftDialog()
        }

        mBinding.apply {
            createNoteList.adapter = groupAdapter
        }
        observeSelectedImages()
        observeNoteSuccessCreated()
        observeDraftSuccessCreated()
        observeNoteDraft()
    }

    private fun initSections(draftModel: NoteDraftModel?) {
        mainDataSection.apply {
            updateGroup(MainDataGroup(requireContext(), draftModel))
        }
        addressDataSection.apply {
            updateItem(
                AddressDataItem(
                    requireActivity(),
                    viewModel.getCurrentTown(),
                    draftModel?.address?.region,
                    draftModel?.address?.metro
                )
            )
        }

        imagesSection.apply {
            updateItem(
                ImagesListItem(null,
                    { viewModel.showGallery() },
                    { viewModel.removeSelectedImage(it) }
                )
            )
        }
        saveButtonSection.updateItem(MainButtonItem(true, getString(R.string.publicate)) {
            createNote()
        })

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

    private fun observeDraftSuccessCreated() {
        viewModel.draftCreated.observe(viewLifecycleOwner) {
            if (it) findNavController().navigateUp()
        }
    }


    private fun observeNoteDraft() {
        viewModel.noteDraft.observe(viewLifecycleOwner) {
            if (it == null) initSections(null)
            else showFillSavedDraftDialog(it)
        }
    }

    private fun showSaveDraftDialog() {
        val mainDataItem = mainDataSection.findGroupBy<MainDataGroup> { true }
        val addressDataItem = addressDataSection.findItemBy<AddressDataItem> { true }
        if (mainDataItem != null && addressDataItem != null && !mainDataItem.isFieldsEmpty()) {
            ActionsMessageDialog(
                requireContext(),
                "Сохранить в черновик?",
                "Записи с полей ввода будут сохранены в черновик. По желанию вы можете использовать их или заполнить поля ввода новыми данными.",
                "Сохранить"
            ).setAcceptCallback {
                viewModel.createNoteDraft(mainDataItem.getDraftData(addressDataItem.getAddressData()))
            }.setCancelCallback { findNavController().navigateUp() }
        } else findNavController().navigateUp()
    }

    private fun showFillSavedDraftDialog(draft: NoteDraftModel?) {
        ActionsMessageDialog(
            requireContext(),
            "Черновик",
            "У вас есть сохраненный черновик. Заполнить поля ввода данными из черновика?",
            "Заполнить",
            false
        )
            .setAcceptCallback { initSections(draft) }
            .setCancelCallback { initSections(null) }
    }

    private fun showHome() {
        if (!findNavController().popBackStack(R.id.home_fragment, false)) {
            findNavController().navigate(R.id.home_fragment, null,
                navOptions {
                    popUpTo(R.id.nav_graph) { inclusive = true }
                })
        }
    }


    override val layoutId: Int = R.layout.fragment_create_note
    override fun getViewModelClass(): KClass<CreateNoteViewModel> = CreateNoteViewModel::class
    override val title: CharSequence by lazy { getString(R.string.new_note) }
    override fun toolbarIconsContainer(viewGroup: ViewGroup) {}
}