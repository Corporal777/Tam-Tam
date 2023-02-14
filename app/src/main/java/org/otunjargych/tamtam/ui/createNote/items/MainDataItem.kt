package org.otunjargych.tamtam.ui.createNote.items

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.core.view.allViews
import androidx.core.view.doOnLayout
import androidx.core.view.forEachIndexed
import com.google.android.material.textfield.TextInputEditText
import com.xwray.groupie.databinding.BindableItem
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ItemMainDataBinding
import org.otunjargych.tamtam.databinding.LayoutPhoneFieldBinding
import org.otunjargych.tamtam.model.request.NoteContactsModel
import org.otunjargych.tamtam.ui.views.adapters.NoFilterArrayAdapter
import org.otunjargych.tamtam.util.extensions.getCategoriesList
import org.otunjargych.tamtam.util.initInput
import org.otunjargych.tamtam.util.setUserImage

class MainDataItem() : BindableItem<ItemMainDataBinding>() {

    var onCategoryChangeCallback: (category: String) -> Unit = {}

    private var noteName = ""
    private var noteDescription = ""
    private var noteCategory = ""
    private val callPhonesList = arrayListOf(PhoneData(""))
    private val whatsAppPhonesList = arrayListOf(PhoneData(""))


    private lateinit var mBinding: ItemMainDataBinding
    override fun bind(viewBinding: ItemMainDataBinding, position: Int) {
        mBinding = viewBinding
        viewBinding.apply {
            etNoteName.initInput(noteName) {
                tilNoteName.error = null
                noteName = it.toString()
            }
            etNoteDescription.initInput(noteDescription) {
                noteDescription = it.toString()
            }
            etNoteCategory.apply {
                keyListener = null
                setAdapter(
                    NoFilterArrayAdapter(
                        context,
                        R.layout.layout_town,
                        android.R.id.text1,
                        getCategoriesList().toMutableList()
                    )

                )
                initInput(noteCategory) {
                    tilNoteCategory.error = null
                    noteCategory = it.toString()
                }
                setOnItemClickListener { _, _, _, _ ->
                    onCategoryChangeCallback.invoke(formatCategory(noteCategory))
                }
            }
            //call phone number
            lnCallNumbers.apply {
                removeAllViews()
                callPhonesList.forEach {
                    initCallPhoneField(viewBinding, it)
                }
            }
            tvAddCallPhone.setOnClickListener {
                if (!callPhonesList.lastOrNull()?.value.isNullOrBlank()) {
                    PhoneData(value = "").apply {
                        callPhonesList.add(this)
                        initCallPhoneField(viewBinding, this)
                    }
                } else {
                    if (!lnCallNumbers.allViews.toList().isNullOrEmpty()) {
                        lnCallNumbers.findViewById<EditText>(R.id.et_phone).requestFocus()
                    }
                }
            }

            //whats app number
            lnWhatsappNumbers.apply {
                removeAllViews()
                whatsAppPhonesList.forEach {
                    initWhatsAppPhoneField(viewBinding, it)
                }
            }
            tvAddWhatsAppPhone.setOnClickListener {
                if (!whatsAppPhonesList.lastOrNull()?.value.isNullOrBlank()) {
                    PhoneData(value = "").apply {
                        whatsAppPhonesList.add(this)
                        initWhatsAppPhoneField(viewBinding, this)
                    }
                } else {
                    if (!lnWhatsappNumbers.allViews.toList().isNullOrEmpty()) {
                        lnWhatsappNumbers.findViewById<EditText>(R.id.et_phone).requestFocus()
                    }
                }
            }
            isMainDataValid()
        }
    }


    private fun initCallPhoneField(viewBinding: ItemMainDataBinding, phone: PhoneData) {
        val binding = LayoutPhoneFieldBinding.inflate(
            LayoutInflater.from(viewBinding.root.context),
            viewBinding.lnCallNumbers,
            false
        )
        binding.etPhone.apply {
            filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
                source.toString().filterNot { it.isWhitespace() }
            })
            initInput(phone.value) { phone.value = it?.toString() ?: "" }
        }
        binding.ivDelete.setOnClickListener {
            binding.etPhone.text?.clear()
            if (callPhonesList.size < 2) {
                phone.value = ""
            } else {
                if (callPhonesList.remove(phone)) {
                    viewBinding.lnCallNumbers.removeView(binding.root)
                }
            }
        }

        viewBinding.lnCallNumbers.addView(binding.root)
    }

    private fun initWhatsAppPhoneField(viewBinding: ItemMainDataBinding, phone: PhoneData) {
        val binding = LayoutPhoneFieldBinding.inflate(
            LayoutInflater.from(viewBinding.root.context),
            viewBinding.lnWhatsappNumbers,
            false
        )
        binding.etPhone.apply {
            filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
                source.toString().filterNot { it.isWhitespace() }
            })
            initInput(phone.value) { phone.value = it?.toString() ?: "" }
        }
        binding.ivDelete.setOnClickListener {
            binding.etPhone.text?.clear()
            if (whatsAppPhonesList.size < 2) {
                phone.value = ""
            } else {
                if (whatsAppPhonesList.remove(phone)) {
                    viewBinding.lnWhatsappNumbers.removeView(binding.root)
                }
            }
        }

        viewBinding.lnWhatsappNumbers.addView(binding.root)
    }

    private fun formatCategory(category: String): String {
        if (category == "Работа, Вакансии") return "work"
        else return ""
    }


    fun isMainDataValid(): Boolean {
        var valid = true
        if (noteName.isNullOrBlank()) {
            valid = false
            mBinding.apply {
                tilNoteName.error = root.context.getString(R.string.require_enter)
            }
        }
        if (noteCategory.isNullOrBlank()) {
            valid = false
            mBinding.apply {
                tilNoteCategory.error = root.context.getString(R.string.require_enter)
            }

        }
        return valid
    }

    fun getMainData(): MutableMap<String, String> {
        val data = mutableMapOf<String, String>()
        data["name"] = noteName
        data["description"] = noteDescription
        data["category"] = formatCategory(noteCategory)

        return data
    }

    fun getContactsData(): NoteContactsModel {
        return NoteContactsModel(
            getCallPhones(),
            getWhatsAppPhones(),
            emptyList()
        )
    }

    private fun getWhatsAppPhones(): List<String> {
        return whatsAppPhonesList.filter { x -> !x.value.isNullOrBlank() }.map { x -> x.value }
    }

    private fun getCallPhones(): List<String> {
        return callPhonesList.filter { x -> !x.value.isNullOrBlank() }.map { x -> x.value }
    }


    override fun getLayout(): Int = R.layout.item_main_data
}

class PhoneData(
    var value: String
)