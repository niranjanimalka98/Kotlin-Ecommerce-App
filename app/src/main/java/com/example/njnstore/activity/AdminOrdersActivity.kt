package com.example.njnstore.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.njnstore.R
import com.example.njnstore.adapter.OrdersAdapter
import com.example.njnstore.model.OrderModel
import com.google.firebase.firestore.FirebaseFirestore

class AdminOrdersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_orders)
        window.statusBarColor = resources.getColor(R.color.light_blue_900)
        window.navigationBarColor = resources.getColor(R.color.light_blue_900)

        initToolbar()
        get_data()
    }

    var progressBar: ProgressBar? = null
    private var recyclerView: RecyclerView? = null
    private fun initToolbar() {
        progressBar = findViewById(R.id.progress_bar)
        recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        recyclerView!!.visibility = View.GONE
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Online Store")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setSystemBarColor(this)
    }

    var orders: List<OrderModel> = ArrayList<OrderModel>()
    var db = FirebaseFirestore.getInstance()
    private fun get_data() {
        db.collection("CUSTOMER_ORDERS").get().addOnSuccessListener { queryDocumentSnapshots ->
            orders = queryDocumentSnapshots.toObjects(OrderModel::class.java)
            initComponents()
        }.addOnFailureListener { initComponents() }
    }

    private var mAdapter: OrdersAdapter? = null
    private fun initComponents() {
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        //recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 8), true));
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.isNestedScrollingEnabled = false
        progressBar!!.visibility = View.GONE
        recyclerView!!.visibility = View.VISIBLE
        mAdapter = OrdersAdapter(orders, this, "0")
        recyclerView!!.adapter = mAdapter
        mAdapter!!.SetOnItemClickListener(object : OrdersAdapter.OnItemClickListener {
            override fun onItemClick(view: View?, obj: OrderModel?, pos: Int) {
                val i = Intent(this@AdminOrdersActivity, AdminOrderActivity::class.java)
                i.putExtra("order_id", obj?.order_id)
                this@AdminOrdersActivity.startActivity(i)
            }
        })
    }

    fun setSystemBarColor(act: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = act.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }
}