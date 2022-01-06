package org.otunjargych.tamtam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.otunjargych.tamtam.databinding.ListItemBinding
import org.otunjargych.tamtam.extensions.asTime
import org.otunjargych.tamtam.extensions.boom.Boom
import org.otunjargych.tamtam.extensions.toastMessage
import org.otunjargych.tamtam.model.Note

class PagingAdapter : PagingDataAdapter<Note, PagingAdapter.PagingViewHolder>(diffUtil) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PagingAdapter.PagingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemBinding.inflate(inflater, parent, false)
        return PagingViewHolder(binding)
    }


    override fun onBindViewHolder(holder: PagingAdapter.PagingViewHolder, position: Int) {
        val node = getItem(position)
        with(holder) {
            binding.tvText.text = node?.text
            bind(node!!)
        }
    }



    inner class PagingViewHolder(val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) = with(binding) {
            toastMessage(binding.root.context, note.uuid)
            if (note.text.length > 80) {
                tvText.text =
                    StringBuilder(
                        note.text.substring(0, 79).replace("\n", " ").toLowerCase()
                    ).append("...")
                        .toString()
            } else
                tvText.text = note.text
            if (note.salary == "" || note.salary == null) {
                tvSalary.text = "Договорная"
                ivCurrency.visibility = View.GONE
            } else
                tvSalary.text = note.salary

            tvDate.text = note.timeStamp.toString().asTime()
            if (note.station == null || note.station == "") {
                tvLocation.text = "Не указано"
            } else
                tvLocation.text = "м. " + note.station
            tvCategory.text = note.category
            if (note.images.size > 0) {
                Picasso.get().load(note.images[0]).into(ivNoteImage)
            }
            Boom(cvItem)
        }


    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem.text == newItem.text
            }

            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem == newItem
            }

        }
    }
}