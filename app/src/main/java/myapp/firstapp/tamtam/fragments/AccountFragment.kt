package myapp.firstapp.tamtam.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import coil.load
import myapp.firstapp.tamtam.model.User
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import myapp.firstapp.tamtam.R
import myapp.firstapp.tamtam.activities.RegistrationActivity
import myapp.firstapp.tamtam.extensions.*

class AccountFragment : Fragment() {
    private lateinit var mImageViewUserPhoto: CircleImageView
    private lateinit var mTextViewName: TextView
    private lateinit var mTextViewPhone: TextView
    private lateinit var mTextViewEmail: TextView
    private lateinit var mRefUser: DatabaseReference
    private lateinit var mRefListener: AppValueEventListener
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()
    private var uid = FirebaseAuth.getInstance().currentUser?.uid
    private var imageUrl: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_account, container, false)
        mTextViewName = view.findViewById(R.id.tv_user_name)
        mTextViewPhone = view.findViewById(R.id.tv_user_phone_number)
        mTextViewEmail = view.findViewById(R.id.tv_user_email)
        mImageViewUserPhoto = view.findViewById(R.id.iv_user_photo)
        val mToolBar: MaterialToolbar = view.findViewById(R.id.account_toolbar)
        (activity as AppCompatActivity?)!!.setSupportActionBar(mToolBar)
        mToolBar.setNavigationIcon(R.drawable.ic_back)
        mToolBar.setTitle("Аккаунт")
        mToolBar.setNavigationOnClickListener {
            fragmentManager?.popBackStack()
        }

        return view
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.account_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.change_name -> {
                if (hasConnection(context!!)) {
                    replaceFragment(ChangeNameFragment())
                    return true
                } else {
                    Snackbar.make(view!!, "Нет интернет соединения!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(resources.getColor(R.color.app_main_color)).show()
                    return false
                }
            }

            R.id.change_photo -> {
                if (hasConnection(context!!)) {
                    val intent: Intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, 1)
                    return true
                } else {
                    Snackbar.make(view!!, "Нет интернет соединения!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(resources.getColor(R.color.app_main_color)).show()
                    return false
                }
            }
            R.id.exit -> {
                if (hasConnection(context!!)) {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(activity, RegistrationActivity::class.java))
                    activity?.finish()
                    return true
                } else {
                    Snackbar.make(view!!, "Нет интернет соединения!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(resources.getColor(R.color.app_main_color)).show()
                    return false
                }
            }
            else -> false
        }
        return super.onOptionsItemSelected(item)
    }

    private fun changePhoto(url: Uri) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val mRefUsers =
            FirebaseDatabase.getInstance().reference.child(NODE_USERS).child(uid.toString())
        REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
        val path =
            FirebaseStorage.getInstance().reference.child(FOLDER_USER_IMAGES).child(uid.toString())
        path.putFile(url).addOnCompleteListener {
            if (it.isSuccessful) {
                path.downloadUrl.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        imageUrl = task.result.toString()

                        mRefUser = FirebaseDatabase.getInstance().getReference(NODE_USERS)
                            .child(uid.toString())
                        mRefListener = AppValueEventListener { snapShot ->
                            if (snapShot != null) {
                                val image: DatabaseReference = snapShot.ref.child("image")
                                image.setValue(imageUrl)
                                mImageViewUserPhoto.load(imageUrl)
                                successToast("Фото изменено", activity!!)
                            }
                        }
                        mRefUsers.addListenerForSingleValueEvent(mRefListener)
                        mapListeners[mRefUsers] = mRefListener
                    }
                }
            }
        }


    }

    private fun initUserInfo() {
        mRefUser = FirebaseDatabase.getInstance().getReference().child(NODE_USERS).child(uid!!)
        mRefListener = AppValueEventListener { snapShot ->

            if (snapShot != null) {
                val user: User? = snapShot.getValue(User::class.java)
                if (user?.name == null || user?.last_name == "")
                    mTextViewName.setText("Не указано")
                else
                    mTextViewName.setText(user?.name + " " + user?.last_name)

                if (user?.phone_number == null || user?.phone_number == "")
                    mTextViewPhone.setText("Не указано")
                else
                    mTextViewPhone.setText(user?.phone_number)

                if (user?.email == null || user?.email == "")
                    mTextViewEmail.setText("Не указано")
                else
                    mTextViewEmail.setText(user?.email)

                if (user?.image == null || user?.image == "")
                    mImageViewUserPhoto.load(R.drawable.camera)
                else
                    mImageViewUserPhoto.load(user?.image)
                //Glide.with(activity!!).load(user?.image).into(mImageViewUserPhoto)
            }
        }
        mRefUser.addListenerForSingleValueEvent(mRefListener)
        mapListeners[mRefUser] = mRefListener
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        initUserInfo()
    }

    override fun onPause() {
        super.onPause()
        mapListeners.forEach {
            it.key.removeEventListener(it.value)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                val data: Uri = data.data!!
                changePhoto(data)
            }
        }
    }
}