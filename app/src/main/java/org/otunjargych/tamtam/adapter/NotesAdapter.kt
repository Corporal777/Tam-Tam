package org.otunjargych.tamtam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.otunjargych.tamtam.databinding.ListItemBinding
import org.otunjargych.tamtam.extensions.DiffUtilCallbackN
import org.otunjargych.tamtam.extensions.boom.Boom
import org.otunjargych.tamtam.model.Note
import java.text.SimpleDateFormat
import java.util.*

class NotesAdapter : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    private var listNotes = mutableListOf<Note>()
    private lateinit var onClickListener: OnNoteClickListener
    private lateinit var mDiffResult: DiffUtil.DiffResult

    interface OnNoteClickListener {
        fun onNoteClick(note: Note, position: Int)
    }

    fun update(list: List<Note>, onClickListener: NotesAdapter.OnNoteClickListener) {
        listNotes.clear()
        listNotes.addAll(list)
        this.onClickListener = onClickListener
        mDiffResult = DiffUtil.calculateDiff(DiffUtilCallbackN(listNotes, list))
        mDiffResult.dispatchUpdatesTo(this)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            bind(listNotes[position])
        }
    }

    override fun getItemCount(): Int {
        return listNotes.size
    }

    inner class ViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
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
            if (note.images.size > 0){
                Picasso.get().load(note.images[0]).into(ivNoteImage)
            }
            Boom(cvItem)
            cvItem.setOnClickListener {
                onClickListener.onNoteClick(note, position)

            }

        }
    }

    fun String.asTime(): String {
        val time = Date(this.toLong())
        val timeFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return timeFormat.format(time)
    }

}