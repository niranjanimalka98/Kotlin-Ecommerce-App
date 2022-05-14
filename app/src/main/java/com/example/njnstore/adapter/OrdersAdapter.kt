package com.example.njnstore.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.njnstore.R
import com.example.njnstore.model.OrderModel

class OrdersAdapter(items: List<OrderModel>, context: Context, layout_style: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<OrderModel> = ArrayList<OrderModel>()
    private var mOnItemClickListener: OnItemClickListener? = null
    var context: Context
    var layout_style = "0"
    fun SetOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = mItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View?, obj: OrderModel?, pos: Int)
    }

    inner class OriginalViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var image: ImageView
        var customer_name: TextView
        var customer_address: TextView
        var total_price: TextView
        var lyt_parent: View

        init {
            image = v.findViewById<View>(R.id.image) as ImageView
            customer_name = v.findViewById<View>(R.id.cust_name) as TextView
            customer_address = v.findViewById<View>(R.id.cust_address) as TextView
            total_price = v.findViewById<View>(R.id.total_price) as TextView
            lyt_parent = v.findViewById(R.id.lyt_parent) as View
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        val v: View
        if (layout_style == "0") {
            v = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        } else if (layout_style == "1") {
            v = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        } else {
            v = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        }
        vh = OriginalViewHolder(v)
        return vh
    }

    var total = 0
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        if (holder is OriginalViewHolder) {
            val view = holder
            val o: OrderModel = items[position]
            view.customer_name.setText(o.customer.first_name.toString() + " " + o.customer.last_name)
            view.customer_address.setText(o.customer.address)
            total = 0
            for (c in o.cart) {
                total += c.product_price
            }
            view.total_price.text = "Rs: $total"


//            Glide.with(context)
//                    .load(o.customer.profile_photo)
//
//                    .into(view.image);
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