package org.otunjargych.tamtam.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.otunjargych.tamtam.databinding.PageItemBinding


class ViewPagerAdapter() :
    RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {

    private var imageList = mutableListOf<String>()
    private lateinit var onImageClickListener: OnImageClickListener


    @SuppressLint("NotifyDataSetChanged")
    fun update(list: MutableList<String>, onImageClickListener: OnImageClickListener) {
        imageList.clear()
        imageList.addAll(list)
        this.onImageClickListener = onImageClickListener
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PageItemBinding.inflate(inflater, parent, false)
        return ViewPagerViewHolder(binding)
    }

    interface OnImageClickListener {
        fun onImageClick()
    }



    override fun getItemCount(): Int {
        return imageList.size
    }



    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    inner class ViewPagerViewHolder(private val binding: PageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(url: String?) = with(binding) {
//            if (url == "" || url == null) {
//                Glide.with(context).load(R.drawable.placeholder).into(iv_paging_images)
//            } else{
//                Glide.with(context).load(url).into(iv_paging_images)
//                iv_paging_images.setOnClickListener {
//                    onImageClickListener.onImageClick()
//                }
//            }
            Picasso.get().load(url).into(binding.ivPagingImages)


        }
    }
}