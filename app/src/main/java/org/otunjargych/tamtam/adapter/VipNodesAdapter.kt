package org.otunjargych.tamtam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ItemVipNodeBinding
import org.otunjargych.tamtam.model.Node
import org.otunjargych.tamtam.util.extensions.DiffUtilCallbackN
import org.otunjargych.tamtam.util.extensions.OnNodeClickListener
import org.otunjargych.tamtam.util.extensions.boom.Boom
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class VipNodesAdapter : RecyclerView.Adapter<VipNodesAdapter.ViewHolder>() {

    private var listNotes = ArrayList<Node>()
    private lateinit var onClickListener: OnNodeClickListener
    private lateinit var mDiffResult: DiffUtil.DiffResult


    fun update(list: List<Node>, onClickListener: OnNodeClickListener) {
        listNotes.clear()
        listNotes.addAll(list)
        this.onClickListener = onClickListener
        mDiffResult = DiffUtil.calculateDiff(DiffUtilCallbackN(listNotes, list))
        mDiffResult.dispatchUpdatesTo(this)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemVipNodeBinding.inflate(inflater, parent, false)
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


    inner class ViewHolder(private val binding: ItemVipNodeBinding) :
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

    fun String.asTime(): String {
        val time = Date(this.toLong())
        val timeFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return timeFormat.format(time)
    }

}