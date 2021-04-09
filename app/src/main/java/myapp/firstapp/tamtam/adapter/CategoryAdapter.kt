package myapp.firstapp.tamtam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.category_item.view.*
import myapp.firstapp.tamtam.R

class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var category_list = ArrayList<String>()
    private lateinit var onClickListener: OnCategoryClickListener

    interface OnCategoryClickListener {
        fun onCategoryClick(category: String, position: Int)
    }

    fun update(list: List<String>, onClickListener: OnCategoryClickListener) {
        category_list = list as ArrayList<String>
        this.onClickListener = onClickListener
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(category_list[position])
    }

    override fun getItemCount(): Int {
        return category_list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(category: String) = with(itemView) {
            tv_name_of_category.text = category
            if (category == "Транспорт, Перевозки")
                iv_icon_of_category.setImageResource(R.drawable.transport)
            if (category == "Медицина, Красота")
                iv_icon_of_category.setImageResource(R.drawable.medicine)
            if (category == "Недвижимость, Квартиры")
                iv_icon_of_category.setImageResource(R.drawable.house_icon)
            if (category == "Продажа, Покупка")
                iv_icon_of_category.setImageResource(R.drawable.buy_sell)
            if (category == "Квартиры, Гостиницы")
                iv_icon_of_category.setImageResource(R.drawable.house_icon)
            if (category == "Обучение, Услуги")
                iv_icon_of_category.setImageResource(R.drawable.icon_study)

            ln_chosen_category.setOnClickListener {
                iv_chosen_icon.visibility = View.VISIBLE
                onClickListener.onCategoryClick(category, position)
            }
        }


    }

}