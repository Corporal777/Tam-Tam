package myapp.firstapp.tamtam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.page_item.view.*
import myapp.firstapp.tamtam.R


class ViewPagerAdapter() :
    RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {

    private var imageList = mutableListOf<String>()
    private lateinit var onImageClickListener: OnImageClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.page_item, parent, false)
        return ViewPagerViewHolder(view)
    }

    interface OnImageClickListener {
        fun onImageClick()
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    fun update(list: MutableList<String>, onImageClickListener: OnImageClickListener) {
        imageList = list
        this.onImageClickListener = onImageClickListener
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    inner class ViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(url: String?) = with(itemView) {
            if (url == "" || url == null) {
                Glide.with(context).load(R.drawable.placeholder).into(iv_paging_images)
            } else{
                Glide.with(context).load(url).into(iv_paging_images)
                iv_paging_images.setOnClickListener {
                    onImageClickListener.onImageClick()
                }
            }


        }
    }
}