package org.otunjargych.tamtam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.otunjargych.tamtam.databinding.ListItemBinding
import org.otunjargych.tamtam.extensions.OnNodeClickListener
import org.otunjargych.tamtam.extensions.asTime
import org.otunjargych.tamtam.extensions.boom.Boom
import org.otunjargych.tamtam.model.Node

class PagingNodesAdapter(private val listener: OnNodeClickListener) :
    PagingDataAdapter<Node, PagingNodesAdapter.PagingViewHolder>(diffUtil) {

    private lateinit var onClickListener: OnNodeClickListener

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PagingNodesAdapter.PagingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemBinding.inflate(inflater, parent, false)
        return PagingViewHolder(binding)
    }


    override fun onBindViewHolder(holder: PagingNodesAdapter.PagingViewHolder, position: Int) {
        this.onClickListener = listener
        val node = getItem(position)
        with(holder) {
            bind(node!!)
        }
    }


    inner class PagingViewHolder(val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(node: Node) = with(binding) {
            if (node.text.length > 80) {
                tvText.text =
                    StringBuilder(
                        node.text.substring(0, 79).replace("\n", " ").toLowerCase()
                    ).append("...")
                        .toString()
            } else
                tvText.text = node.text
            if (node.salary == "" || node.salary == null) {
                tvSalary.text = "Договорная"
                ivCurrency.visibility = View.GONE
            } else
                tvSalary.text = node.salary

            tvDate.text = node.timeStamp.toString().asTime()
            if (node.station == null || node.station == "") {
                tvLocation.text = "Не указано"
            } else
                tvLocation.text = "м. " + node.station
            tvCategory.text = node.category
            if (node.images.size > 0) {
                Picasso.get().load(node.images[0]).into(ivNoteImage)
            }
            Boom(cvItem)
            cvItem.setOnClickListener {
                onClickListener.onNodeClick(node, position)

            }
        }


    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Node>() {
            override fun areItemsTheSame(oldItem: Node, newItem: Node): Boolean {
                return oldItem.timeStamp == newItem.timeStamp
            }

            override fun areContentsTheSame(oldItem: Node, newItem: Node): Boolean {
                return oldItem == newItem
            }

        }
    }
}