package org.otunjargych.tamtam.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.paging.DatabasePagingOptions
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter
import org.otunjargych.tamtam.databinding.ListItemBinding
import org.otunjargych.tamtam.model.Note

class FirebasePagingAdapter(options: DatabasePagingOptions<Note>) :

    FirebaseRecyclerPagingAdapter<Note, ViewHolder>(options) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int, model: Note) {
        with(viewHolder) {
            bind(model)
        }
    }
}
