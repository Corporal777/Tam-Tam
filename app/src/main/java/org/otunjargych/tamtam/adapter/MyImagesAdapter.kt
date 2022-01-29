package org.otunjargych.tamtam.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.otunjargych.tamtam.databinding.ItemChosenImagesBinding

class MyImagesAdapter : RecyclerView.Adapter<MyImagesAdapter.ViewHolder>() {

    private var listImages = ArrayList<String>()
    private var listener: OnImageDeleteListener? = null

    fun addImages(list: List<String>, deleteListener: OnImageDeleteListener) {
        this.listener = deleteListener
        listImages.clear()
        listImages.addAll(list)
        notifyDataSetChanged()

    }

    fun addImage(image: String) {
        if (!listImages.contains(image) && listImages.size < 6) {
            listImages.add(image)
            notifyDataSetChanged()
        }
    }

    interface OnImageDeleteListener {
        fun onImageDelete(image: String, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChosenImagesBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            bind(listImages[position])
        }
    }

    override fun getItemCount(): Int {
        return listImages.size
    }

    inner class ViewHolder(private val binding: ItemChosenImagesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(url: String) = with(binding) {
            Glide.with(binding.root).load(url).into(image)
            ivDelete.setOnClickListener {
                removeAt(adapterPosition)
                listener?.onImageDelete(url, position)
            }
        }

    }

    fun removeAt(position: Int) {
        listImages.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listImages.size)
    }

}