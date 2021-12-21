package org.otunjargych.tamtam.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.api.FireBaseHelper
import org.otunjargych.tamtam.databinding.FragmentNewAdBinding
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.fragments.dialog_fragments.MyDialogFragment
import org.otunjargych.tamtam.model.Note


class AdFragment : BaseFragment() {


    private lateinit var dialog: MyDialogFragment
    private lateinit var mListPopupWindow: ListPopupWindow
    private var mHandler: Handler? = null
    private var mRunnable: Runnable? = null


    private lateinit var userId: String
    private var selectedStation: String? = ""
    private var mSelectedCategory: String? = ""
    private var listener: OnBottomAppBarStateChangeListener? = null

    private var _binding: FragmentNewAdBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewAdBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as OnBottomAppBarStateChangeListener
        } catch (e: Exception) {
            throw ClassCastException("Activity not attached!")
        }
    }

    private fun changeStateBottomAppBar() {
        listener?.onHide()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!hasConnection(requireContext())) {
            Snackbar.make(view, "Нет интернет соединения!", Snackbar.LENGTH_LONG)
                .setBackgroundTint(resources.getColor(R.color.app_main_color)).show()
        }
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.customTitle.text = "Объявление"

        changeStateBottomAppBar()

        binding.tvMetro.setOnClickListener {
            val manager: FragmentManager? = fragmentManager
            dialog = MyDialogFragment()
            dialog.show(manager!!, "metro")


        }

        binding.tvCategory.setOnClickListener {
            showCategoryList()
        }

        binding.btnAddNewAd.setOnClickListener {

            if (binding.tvText.text.isNullOrEmpty() || mSelectedCategory.isNullOrEmpty()) {
                errorToast("Заполните все поля!", activity!!)

            } else {
                addToFB()
                Snackbar.make(it, "Объявление добавлено", Snackbar.LENGTH_LONG)
                    .setAction("Назад") {
                        requireActivity().supportFragmentManager.popBackStack()
                    }.setBackgroundTint(resources.getColor(R.color.app_main_color))
                    .setActionTextColor(resources.getColor(R.color.white))
                    .show()
                mHandler = Handler()
                mRunnable = Runnable {
                    requireActivity().supportFragmentManager.popBackStack()
                }
                mHandler!!.postDelayed(mRunnable!!, 1000)
            }


        }
    }

    private fun showCategoryList() {
        val categoriesList = ArrayList<String>()
        categoriesList.add("Работа, Подработки")
        categoriesList.add("Транспорт, Перевозки")
        categoriesList.add("Медицина, Красота")
        categoriesList.add("Продажа, Покупка")
        categoriesList.add("Квартиры, Гостиницы")
        categoriesList.add("Обучение, Услуги")

        mListPopupWindow = ListPopupWindow(requireContext())
        mListPopupWindow.apply {
            anchorView = binding.tvCategory
            width = 600
            height = 500
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.category_item, R.id.tv_category, categoriesList
                )
            )
            isModal = true
            setOnItemClickListener { parent, view, position, id ->
                binding.tvCategory.setText(categoriesList[position])
                mSelectedCategory = categoriesList[position]
                dismiss()
            }
        }
        mListPopupWindow.show()

    }

    fun setSelectedItem(selectedItem: String?) {
        val tvMetro: TextView = view!!.findViewById(R.id.tv_metro)
        selectedStation = selectedItem
        tvMetro.text = "    м. $selectedItem"
        mHandler = Handler()
        mRunnable = Runnable {
            dialog.dismiss()
        }
        mHandler!!.postDelayed(mRunnable!!, 500)
    }

    private fun addToFB() {

        val title = binding.tvTitle.text.toString()
        val text = binding.tvText.text.toString()
        val salary = binding.tvSalary.text.toString()
        val phoneNumber = binding.tvPhoneNumber.text.toString()

        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        val workCategory = "Работа, Подработки"
        val transportCategory = "Транспорт, Перевозки"
        val medicineCategory = "Медицина, Красота"
        val buySellCategory = "Продажа, Покупка"
        val flatsCategory = "Квартиры, Гостиницы"
        val studyCategory = "Обучение, Услуги"
        val uuid = java.util.UUID.randomUUID().toString()
        val list = ArrayList<String>()
        list.add(0, "Hello")
        list.add(1, "World")

        val time = System.currentTimeMillis()

        val note = Note(
            userId,
            uuid,
            title,
            text,
            salary,
            time,
            selectedStation!!,
            mSelectedCategory!!,
            0,
            0,
            list,
            phoneNumber
        )

        when (mSelectedCategory) {
            medicineCategory -> {
                FireBaseHelper.addNewData(NODE_BEAUTY, note)
            }
            workCategory -> {

                FireBaseHelper.addNewData(NODE_WORKS, note)
            }
            studyCategory -> {

                FireBaseHelper.addNewData(NODE_SERVICES, note)
            }
            flatsCategory -> {

                FireBaseHelper.addNewData(NODE_HOUSE, note)
            }
            transportCategory -> {

                FireBaseHelper.addNewData(NODE_TRANSPORT, note)
            }
            buySellCategory -> {
                FireBaseHelper.addNewData(NODE_BUY_SELL, note)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}




