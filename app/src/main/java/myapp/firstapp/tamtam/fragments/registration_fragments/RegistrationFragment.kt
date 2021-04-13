package myapp.firstapp.tamtam.fragments.registration_fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import coil.load
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import myapp.firstapp.tamtam.MainActivity
import myapp.firstapp.tamtam.R
import myapp.firstapp.tamtam.extensions.*
import myapp.firstapp.tamtam.model.User


class RegistrationFragment : Fragment() {
    private lateinit var mEditTextPhoneNumber: EditText
    private lateinit var mEditTextPassword: EditText
    private lateinit var mEditTextEmail: EditText
    private lateinit var mEditTextName: EditText
    private lateinit var mEditTextLastName: EditText
    private lateinit var mPhoneNumber: String
    private lateinit var mPassword: String
    private lateinit var mName: String
    private lateinit var mLastName: String
    private lateinit var mEmail: String
    private lateinit var mButtonAccept: MaterialButton
    private lateinit var mRefUsers: DatabaseReference
    private lateinit var mTextViewEnter: TextView
    private lateinit var mImageView: CircleImageView
    private var userPhoto: Uri? = null
    private var imageUrl: String = ""


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_phone_reg, container, false)
        mEditTextPhoneNumber = view.findViewById(R.id.et_login_reg)
        mEditTextPassword = view.findViewById(R.id.et_password_reg)
        mEditTextEmail = view.findViewById(R.id.et_email_reg)
        mEditTextName = view.findViewById(R.id.et_name_reg)
        mEditTextLastName = view.findViewById(R.id.et_last_name_reg)
        mButtonAccept = view.findViewById(R.id.btn_reg_accept)
        mTextViewEnter = view.findViewById(R.id.tv_acc_already_exists)
        mImageView = view.findViewById(R.id.user_photo)

        mTextViewEnter.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.registration_container, EnterFragment())?.commit()
        }

        mButtonAccept.setOnClickListener {
            authUser()
        }
        mImageView.setOnClickListener {
            val intent: Intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                mImageView.load(data.data)
                userPhoto = data.data



            }
        }
    }

    private fun authUser() {

        mPhoneNumber = mEditTextPhoneNumber.text.toString() + "@gmail.com"
        mPassword = mEditTextPassword.text.toString()

        AUTH = FirebaseAuth.getInstance()
        AUTH.createUserWithEmailAndPassword(mPhoneNumber, mPassword).addOnCompleteListener {}
                .addOnSuccessListener {
                    addUser()
                    successToast("Добро пожаловать!", activity!!)
                    startActivity(Intent(activity, MainActivity::class.java))
                    activity!!.finish()
                }.addOnFailureListener {
                    errorToast("Профиль не создан!", activity!!)
                }

        AUTH.signInWithEmailAndPassword(mPhoneNumber, mPassword)

    }


    fun addUser() {

        mPhoneNumber = mEditTextPhoneNumber.text.toString()
        mPassword = mEditTextPassword.text.toString()
        mEmail = mEditTextEmail.text.toString()
        mName = mEditTextName.text.toString()
        mLastName = mEditTextLastName.text.toString()
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        mRefUsers = FirebaseDatabase.getInstance().getReference()


        REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
        val path = REF_STORAGE_ROOT.child(FOLDER_USER_IMAGES).child(uid)
        if (userPhoto != null){
            path.putFile(userPhoto!!).addOnCompleteListener {
                if (it.isSuccessful) {
                    path.downloadUrl.addOnCompleteListener {task ->
                        if (task.isSuccessful){
                            imageUrl = task.result.toString()
                            val user: User = User(uid, mName, mLastName, mPhoneNumber, mEmail, imageUrl)
                            mRefUsers.child(NODE_USERS).child(uid).setValue(user)
                        }
                    }
                }
            }
        }else{
            val user: User = User(uid, mName, mLastName, mPhoneNumber, mEmail, imageUrl)
            mRefUsers.child(NODE_USERS).child(uid).setValue(user)
        }


    }
}
