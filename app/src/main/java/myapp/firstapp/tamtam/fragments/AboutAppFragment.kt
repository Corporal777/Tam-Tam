package myapp.firstapp.tamtam.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import myapp.firstapp.tamtam.R


class AboutAppFragment : Fragment() {

    private lateinit var mToolBar : MaterialToolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_about_app, container, false)
        mToolBar = view.findViewById(R.id.about_app_toolbar)

        (activity as AppCompatActivity?)!!.setSupportActionBar(mToolBar)
        mToolBar.setNavigationIcon(R.drawable.ic_back)
        mToolBar.setTitle("О программе")
        mToolBar.setNavigationOnClickListener {
            fragmentManager?.popBackStack()
        }

        return view
    }
}