package org.otunjargych.tamtam.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.otunjargych.tamtam.databinding.ListItemBinding
import org.otunjargych.tamtam.extensions.asTime
import org.otunjargych.tamtam.extensions.boom.Boom
import org.otunjargych.tamtam.model.Note

class ViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(note: Note) = with(binding) {

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