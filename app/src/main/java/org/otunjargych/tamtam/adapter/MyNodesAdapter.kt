package org.otunjargych.tamtam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.SwipeMyItemBinding

import org.otunjargych.tamtam.model.Node
import org.otunjargych.tamtam.util.asTime
import org.otunjargych.tamtam.util.extensions.DiffUtilCallbackN
import org.otunjargych.tamtam.util.extensions.boom.Boom

class MyNodesAdapter : RecyclerView.Adapter<MyNodesAdapter.MyNodeViewHolder>() {

    private var listNodes = mutableListOf<Node>()
    private var viewBinderHelper: ViewBinderHelper = ViewBinderHelper()
    private lateinit var onClickListener: OnNodeClickListener
    private lateinit var mDiffResult: DiffUtil.DiffResult

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyNodeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SwipeMyItemBinding.inflate(inflater, parent, false)
        return MyNodeViewHolder(binding)
    }

    interface OnNodeClickListener {
        fun onNodeClick(node: Node, position: Int)
        fun onNodeDelete(node: Node, position: Int)
        fun onNodeEdit(node: Node, position: Int)
    }


    fun update(list: List<Node>, onAdClickListener: OnNodeClickListener) {
        listNodes.clear()
        listNodes.addAll(list)
        onClickListener = onAdClickListener
        this.onClickListener = onClickListener
        mDiffResult = DiffUtil.calculateDiff(DiffUtilCallbackN(listNodes, list))
        mDiffResult.dispatchUpdatesTo(this)
    }

    fun addNode(node: Node) {
        val list = ArrayList<Node>()
        if (!list.contains(node)) {
            list.add(node)
        }
        listNodes.addAll(list)
        mDiffResult = DiffUtil.calculateDiff(DiffUtilCallbackN(listNodes, list))
        mDiffResult.dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(holder: MyNodeViewHolder, position: Int) {
        viewBinderHelper.setOpenOnlyOne(true)
        viewBinderHelper.bind(holder.swipeRevealLayout, listNodes[position].title)

        viewBinderHelper.closeLayout(listNodes[position].title)
        holder.bind(listNodes[position])
    }

    override fun getItemCount(): Int {
        return listNodes.size
    }

    inner class MyNodeViewHolder(val binding: SwipeMyItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val swipeRevealLayout: SwipeRevealLayout = binding.swipeLayout

        fun bind(node: Node) = with(binding) {

            if (node.text.length > 89) {
                tvText.text =
                    StringBuilder(
                        node.text.substring(0, 88).replace("\n", " ").toLowerCase()
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
            } else
                tvLocation.text = "м. " + node.station
            tvCategory.text = node.category
            if (!node.images.isNullOrEmpty() && !node.images[0].equals("") ) {
                Glide.with(binding.root).load(node.images[0]).into(ivNoteImage)
            }else{
                Glide.with(binding.root).load(R.drawable.placeholder).into(ivNoteImage)
            }

            cvSwipeItem.setOnClickListener {
                onClickListener.onNodeClick(node, position)
            }
            Boom(cvDelete)
            cvDelete.setOnClickListener {
                onClickListener.onNodeDelete(node, position)
                listNodes.remove(node)
                notifyItemRemoved(position)
            }
            Boom(cvEdit)
            cvEdit.setOnClickListener {
                onClickListener.onNodeEdit(node, position)
            }
        }
    }



}