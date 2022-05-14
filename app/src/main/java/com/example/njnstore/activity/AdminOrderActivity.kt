package com.example.njnstore.activity

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.njnstore.R
import com.example.njnstore.adapter.ProductAdapter
import com.example.njnstore.model.CartModel
import com.example.njnstore.model.ItemModel
import com.example.njnstore.model.OrderModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore

class AdminOrderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_order)
        context = this
        order_id = intent.getStringExtra("order_id")
        if (order_id == null) {
            Toast.makeText(context, "Order id not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        initToolbar()
        get_data()
    }

    var db = FirebaseFirestore.getInstance()
    var orderModel: OrderModel? = null
    private fun get_data() {
        db.collection("CUSTOMER_ORDERS").document(order_id!!).get().addOnSuccessListener(
            OnSuccessListener { documentSnapshot ->
                if (!documentSnapshot.exists()) {
                    Toast.makeText(context, "Orders not found", Toast.LENGTH_SHORT).show()
                    finish()
                    return@OnSuccessListener
                }
                orderModel = documentSnapshot.toObject(OrderModel::class.java)
                feed_data()
            }).addOnFailureListener(OnFailureListener { e ->
            Toast.makeText(context, "Failed to get orders " + e.message, Toast.LENGTH_SHORT).show()
            finish()
            return@OnFailureListener
        })
    }

    var recyclerView: RecyclerView? = null
    private var mAdapter: ProductAdapter? = null
    var order_id_view: TextView? = null
    var customer_name: EditText? = null
    var customer_address:EditText? = null
    var customer_contact:EditText? = null
    var delete_order: Button? = null
    private fun feed_data() {
        cartModels = orderModel!!.cart
        for (c in cartModels!!) {
            val p = ItemModel()
            p.product_name = c.product_name
            p.category = ""
            p.description = ""
            p.price = c.product_price
            p.quantity = c.quantity
            p.product_id = c.product_id
            p.photo = c.product_photo
            products.add(p)
        }
        recyclerView = findViewById(R.id.cart_products)
        order_id_view = findViewById(R.id.order_id)
        customer_name = findViewById(R.id.customer_name)
        customer_address = findViewById<EditText>(R.id.customer_address)
        customer_contact = findViewById<EditText>(R.id.customer_contact)
        delete_order = findViewById(R.id.delete_order)
        order_id_view?.setText("Order #" + orderModel?.order_id)
        customer_name?.setText(orderModel?.customer?.first_name.toString() + " " + orderModel?.customer?.last_name)
        customer_address?.setText(orderModel?.customer?.address)
        customer_contact?.setText(orderModel?.customer?.phone_number)
        recyclerView?.setLayoutManager(LinearLayoutManager(this))
        //recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 8), true));
        recyclerView?.setHasFixedSize(true)
        recyclerView?.setNestedScrollingEnabled(false)
        mAdapter = ProductAdapter(products, this, "1")
        recyclerView?.setAdapter(mAdapter)
        delete_order?.setOnClickListener(View.OnClickListener {
            db.collection("CUSTOMER_ORDERS").document(order_id!!).delete().addOnSuccessListener(
                OnSuccessListener {
                    Toast.makeText(context, "Order Deleted Successfully", Toast.LENGTH_SHORT).show()
                    finish()
                    return@OnSuccessListener
                }).addOnFailureListener(OnFailureListener { e ->
                Toast.makeText(context, "Failed to delete order" + e.message, Toast.LENGTH_SHORT)
                    .show()
                return@OnFailureListener
            })
        })
    }

    var order_id: String? = null
    var context: Context? = null
    var products: MutableList<ItemModel> = ArrayList<ItemModel>()
    var cartModels: List<CartModel>? = null

    private fun initToolbar() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setNavigationIcon(R.drawable.ic_menu)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Online Store")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setSystemBarColor(this)
    }

    fun setSystemBarColor(act: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = act.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }
}