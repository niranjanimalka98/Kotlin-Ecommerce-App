package com.example.njnstore.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
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
import com.example.njnstore.model.UserModel
import com.example.njnstore.tools.Utils
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore

class CheckOutActivity : AppCompatActivity() {
    var cart_total: TextView? = null
    var total = 0
    var loggedInUser: UserModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_out)

        loggedInUser = Utils.get_logged_in_user()
        if (loggedInUser == null) {
            Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show()
            val i = Intent(this, SignInActivity::class.java)
            this.startActivity(i)
            finish()
            return
        }

        initToolbar()
        get_cart_data()
    }

    var cartModels: List<CartModel>? = null
    var products: MutableList<ItemModel> = ArrayList<ItemModel>()
    private fun get_cart_data() {
        cartModels = try {
            CartModel.listAll(CartModel::class.java)
        } catch (e: Exception) {
            Toast.makeText(this, "Failed" + e.message, Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        if (cartModels == null) {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        total = 0
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
            total += c.product_price
        }
        cart_total = findViewById<TextView>(R.id.cart_total)
        cart_total?.setText("Total: Rs. $total")
        Log.d("test", total.toString())
        feed_cart_data()
    }

    var recyclerView: RecyclerView? = null
    private var mAdapter: ProductAdapter? = null
    var submit_order: Button? = null
    private fun feed_cart_data() {
        recyclerView = findViewById(R.id.cart_products)
        recyclerView?.setLayoutManager(LinearLayoutManager(this))
        //recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 8), true));
        recyclerView?.setHasFixedSize(true)
        recyclerView?.setNestedScrollingEnabled(false)
        mAdapter = ProductAdapter(products, this, "1")
        recyclerView?.setAdapter(mAdapter)
        submit_order = findViewById(R.id.submit_order)
        submit_order?.setOnClickListener(View.OnClickListener { submit_order() })
    }

    var CUSTOMER_ORDERS = "CUSTOMER_ORDERS"
    var db = FirebaseFirestore.getInstance()
    var progressDialog: ProgressDialog? = null
    private fun submit_order() {
        val orderModel = OrderModel()
        orderModel.order_id = db.collection(CUSTOMER_ORDERS).document().id
        orderModel.customer = loggedInUser!!
        orderModel.cart = cartModels!!
        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Placing")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
        db.collection(CUSTOMER_ORDERS).document(orderModel.order_id).set(orderModel)
            .addOnSuccessListener(
                OnSuccessListener {
                    Toast.makeText(
                        this@CheckOutActivity,
                        "Order placed successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressDialog!!.hide()
                    progressDialog!!.dismiss()
                    try {
                        CartModel.deleteAll(CartModel::class.java)
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@CheckOutActivity,
                            "Failed to clean cart",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    finish()
                    return@OnSuccessListener
                }).addOnFailureListener(OnFailureListener { e ->
                progressDialog!!.hide()
                progressDialog!!.dismiss()
                Toast.makeText(
                    this@CheckOutActivity,
                    "Failed to place order" + e.message,
                    Toast.LENGTH_SHORT
                ).show()
                return@OnFailureListener
            })
    }

    private fun initToolbar() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setNavigationIcon(R.drawable.ic_close)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(null)
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