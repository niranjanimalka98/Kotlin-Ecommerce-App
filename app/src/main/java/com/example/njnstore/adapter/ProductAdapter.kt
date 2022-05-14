package com.example.njnstore.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.njnstore.R
import com.example.njnstore.model.CartModel
import com.example.njnstore.model.ItemModel

class ProductAdapter(items: List<ItemModel>, context: Context, layout_style: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<ItemModel> = ArrayList()
    private var mOnItemClickListener: OnItemClickListener? = null
    var context: Context
    var layout_style = "0"
    fun SetOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = mItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View?, obj: ItemModel?, pos: Int)
    }

    inner class OriginalViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var image: ImageView
        var title: TextView
        var delete: ImageView
        var price: TextView
        var pd_qty: TextView
        var lyt_parent: View

        init {
            image = v.findViewById<View>(R.id.image) as ImageView
            title = v.findViewById<View>(R.id.title) as TextView
            delete = v.findViewById<View>(R.id.delete) as ImageView
            price = v.findViewById<View>(R.id.price) as TextView
            pd_qty = v.findViewById<View>(R.id.pd_qty) as TextView
            lyt_parent = v.findViewById(R.id.lyt_parent) as View
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        val v: View
        v = if (layout_style == "0") {
            LayoutInflater.from(parent.context).inflate(R.layout.product_items, parent, false)
        } else if (layout_style == "1") {
            LayoutInflater.from(parent.context).inflate(R.layout.product_items_1, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.product_items_1, parent, false)
        }
        vh = OriginalViewHolder(v)
        return vh
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        if (holder is OriginalViewHolder) {
            val view = holder
            val p = items[position]
            view.title.text = p.product_name + ""
            view.price.text = p.price.toString() + ""
            view.pd_qty.text = "Qty: " + p.quantity.toString() + ""
            view.delete.setOnClickListener {
                val cm = CartModel()
                CartModel.deleteAll(CartModel::class.java, "PRODUCTID = ?", p.product_id)
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
            }
            Glide.with(context)
                .load(p.photo)
                .into(view.image)
            view.lyt_parent.setOnClickListener { view ->
                if (mOnItemClickListener != null) {
                    mOnItemClickListener!!.onItemClick(view, items[position], position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    init {
        this.items = items
        this.context = context
        this.layout_style = layout_style
    }
}