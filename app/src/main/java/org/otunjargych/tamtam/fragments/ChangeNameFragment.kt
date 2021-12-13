package org.otunjargych.tamtam.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.extensions.AppValueEventListener
import org.otunjargych.tamtam.extensions.NODE_USERS
import org.otunjargych.tamtam.extensions.errorToast
import org.otunjargych.tamtam.extensions.successToast


class ChangeNameFragment : Fragment() {

    private lateinit var mRefUser: DatabaseReference
    private lateinit var mRefListener: AppValueEventListener
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()
    private var uid = FirebaseAuth.getInstance().currentUser?.uid

    private lateinit var mImageViewDone: ImageView
    private lateinit var mEditTextName: EditText
    private lateinit var mEditTextLastName: EditText


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_change_name, container, false)

        mImageViewDone = view.findViewById(R.id.iv_done)
        mEditTextName = view.findViewById(R.id.et_name_change)
        mEditTextLastName = view.findViewById(R.id.et_last_name_change)
        val mToolBar: MaterialToolbar = view.findViewById(R.id.change_name_toolbar)
        var name = mEditTextName.getText()
        var last_name = mEditTextLastName.getText()

        if (mEditTextName.text.isNullOrEmpty()) {
            mEditTextName.requestFocus()

        }

        (activity as AppCompatActivity?)!!.setSupportActionBar(mToolBar)
        mToolBar.setNavigationIcon(R.drawable.ic_back)
        mToolBar.setTitle("Изменить имя")
        mToolBar.setNavigationOnClickListener {
            fragmentManager?.popBackStack()
        }

        mImageViewDone.setOnClickListener {
            if (name.isNullOrEmpty() || last_name.isNullOrEmpty()) {
                errorToast("Поля не могут быть пусты!", activity!!)
            } else {
                changeName(name.toString(), last_name.toString())
                successToast("Изменено!", activity!!)
                fragmentManager?.popBackStack()
            }
        }

        return view

    }

    override fun onStart() {
        super.onStart()
    }

    private fun changeName(changedName: String, changedLastName: String) {
        mRefUser = FirebaseDatabase.getInstance().getReference(NODE_USERS).child(uid.toString())
        mRefListener = AppValueEventListener { snapShot ->
            if (snapShot != null) {
                val name: DatabaseReference = snapShot.ref.child("name")
                val last_name: DatabaseReference = snapShot.ref.child("last_name")
                name.setValue(changedName)
                last_name.setValue(changedLastName)
            }

        }
        mRefUser.addListenerForSingleValueEvent(mRefListener)
        mapListeners[mRefUser] = mRefListener
    }

    override fun onPause() {
        super.onPause()
        mapListeners.forEach {
            it.key.removeEventListener(it.value)
        }
    }
}