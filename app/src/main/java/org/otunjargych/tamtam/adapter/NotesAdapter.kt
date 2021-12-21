package org.otunjargych.tamtam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.otunjargych.tamtam.databinding.ListItemBinding
import org.otunjargych.tamtam.extensions.DiffUtilCallbackN
import org.otunjargych.tamtam.model.Note
import java.text.SimpleDateFormat
import java.util.*

class NotesAdapter : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    private var listAds = mutableListOf<Note>()
    private lateinit var onClickListener: NotesAdapter.OnItemAdClickListener
    private lateinit var mDiffResult: DiffUtil.DiffResult

    interface OnItemAdClickListener {
        fun onAdClick(note: Note, position: Int)
    }

    fun update(list: List<Note>, onClickListener: NotesAdapter.OnItemAdClickListener) {
        this.listAds = list as MutableList<Note>
        this.onClickListener = onClickListener
        mDiffResult = DiffUtil.calculateDiff(DiffUtilCallbackN(listAds, list))
        mDiffResult.dispatchUpdatesTo(this)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            bind(listAds[position])
        }
    }

    override fun getItemCount(): Int {
        return listAds.size
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

            cvItem.setOnClickListener {
                onClickListener.onAdClick(note, position)

            }
            if (!note.images[0].isNullOrEmpty()){
                Picasso.get().load(note.images[0]).into(ivNoteImage)
            }

        }
    }

    fun String.asTime(): String {
        val time = Date(this.toLong())
        val timeFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return timeFormat.format(time)
    }

}