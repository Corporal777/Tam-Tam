package myapp.firstapp.tamtam

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import myapp.firstapp.tamtam.fragments.*
import myapp.firstapp.tamtam.fragments.dialog_fragments.CategoryDialogFragment
import myapp.firstapp.tamtam.fragments.dialog_fragments.MyDialogFragment


class MainActivity : AppCompatActivity(), MyDialogFragment.OnFragmentSendDataListener,
        CategoryDialogFragment.OnFragmentSendCategoryListener {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, MainFragment())
                .commit()

    }

    override fun onSendData(data: String?) {
        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (fragment is AdFragment) {
            fragment.setSelectedItem(data)
        }
        if (fragment is WorksFragment) {
            fragment.setSelectedStation(data)
        }
        if (fragment is TransportFragment) {
            fragment.setSelectedStation(data)
        }
        if (fragment is BeautyFragment) {
            fragment.setSelectedStation(data)
        }
        if (fragment is BuySellFragment) {
            fragment.setSelectedStation(data)
        }
        if (fragment is FlatsFragment) {
            fragment.setSelectedStation(data)
        }
        if (fragment is ServicesFragment) {
            fragment.setSelectedStation(data)
        }
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }


    override fun onSendCategory(category: String?) {
        val fragment: AdFragment? = supportFragmentManager
                .findFragmentById(R.id.fragment_container) as AdFragment?
        if (fragment != null) fragment.setSelectedCategory(category)
    }

}