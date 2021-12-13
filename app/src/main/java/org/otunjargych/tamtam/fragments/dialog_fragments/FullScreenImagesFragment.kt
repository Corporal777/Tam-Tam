package org.otunjargych.tamtam.fragments.dialog_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.adapter.FullScreenImageAdapter
import java.io.Serializable

class FullScreenImagesFragment : Fragment() {

    private lateinit var mViewPager: ViewPager2
    private lateinit var adapter: FullScreenImageAdapter
    private lateinit var moreImageList: MutableList<String>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = LayoutInflater.from(context).inflate(R.layout.fragment_full_images, null)
        mViewPager = view.findViewById(R.id.view_pager_full)
        val tab: TabLayout = view.findViewById(R.id.tab_dots)


        moreImageList = ArrayList()

        val image_1: Serializable? = arguments?.getSerializable("image_first")
        val image_2: Serializable? = arguments?.getSerializable("image_second")
        val image_3: Serializable? = arguments?.getSerializable("image_third")
        val image_4: Serializable? = arguments?.getSerializable("image_fourth")


        if (image_1 != "" && image_1 != null) {
            moreImageList.add(image_1.toString())
        }
        if (image_2 != "" && image_2 != null) {
            moreImageList.add(image_2.toString())
        }
        if (image_3 != "" && image_3 != null) {
            moreImageList.add(image_3.toString())
        }
        if (image_4 != "" && image_4 != null) {
            moreImageList.add(image_4.toString())
        }
        adapter = FullScreenImageAdapter(moreImageList)
        mViewPager.adapter = adapter
        TabLayoutMediator(tab, mViewPager) { tab, position ->
        }.attach()
        return view

    }

}