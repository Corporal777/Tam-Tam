package org.otunjargych.tamtam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ItemNodeBinding
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
        val binding = ItemNodeBinding.inflate(inflater, parent, false)
        return PagingViewHolder(binding)
    }


    override fun onBindViewHolder(holder: PagingNodesAdapter.PagingViewHolder, position: Int) {
        this.onClickListener = listener
        val node = getItem(position)
        with(holder) {
            bind(node!!)
        }
    }


    inner class PagingViewHolder(val binding: ItemNodeBinding) :
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
            if (node.salary == "" || node.salary.isNullOrEmpty()) {
                tvSalary.text = "Договорная"
                ivCurrency.visibility = View.GONE
            } else
                tvSalary.text = node.salary

            tvDate.text = node.timeStamp.toString().asTime()
            if (node.station.isNullOrEmpty() || node.station == "") {
                tvLocation.text = "Не указано"
            } else{
                if (node.station.length > 17){
                    tvLocation.text = StringBuilder(
                        node.station.substring(0, 16)).append("...")
                        .toString()
                }else
                    tvLocation.text = "м. " + node.station
            }
            if (!node.category.isNullOrEmpty()){
                tvCategory.text = node.category
            }
            if (!node.images.isNullOrEmpty() && !node.images[0].equals("") ) {
                Glide.with(binding.root).load(node.images[0]).into(ivNoteImage)
            }else{
                Glide.with(binding.root).load(R.drawable.placeholder).into(ivNoteImage)
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