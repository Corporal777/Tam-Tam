package org.otunjargych.tamtam.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.otunjargych.tamtam.databinding.ItemChosenImagesBinding

class ImagesAdapter : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    private var listImages = ArrayList<Uri>()


    fun update(list: List<Uri>) {
        listImages.clear()
        listImages.addAll(list)
        notifyDataSetChanged()

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
        fun bind(url: Uri) = with(binding) {
            Glide.with(binding.root).load(url).into(image)
        }
    }


}