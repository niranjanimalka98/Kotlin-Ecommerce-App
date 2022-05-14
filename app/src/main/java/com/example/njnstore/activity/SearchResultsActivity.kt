package com.example.njnstore.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.njnstore.MainActivity
import com.example.njnstore.R
import com.example.njnstore.adapter.ProductAdapter
import com.example.njnstore.model.ItemModel
import com.example.njnstore.model.UserModel
import com.example.njnstore.tools.Utils
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class SearchResultsActivity : AppCompatActivity() {

    var loggedInUser: UserModel? = null

    private var recyclerView: RecyclerView? = null
    private var home: LinearLayout? = null
    private var search: LinearLayout? = null
    private var shopping: LinearLayout? = null
    private var setting: LinearLayout? = null
    private var user: LinearLayout? = null
    var search_string: String? = null

    private var mAdapter: ProductAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)
        window.statusBarColor = resources.getColor(R.color.light_blue_900)
        window.navigationBarColor = resources.getColor(R.color.light_blue_900)

        search_string = intent.getStringExtra("search_id")!!.toLowerCase()

        Log.d("MYTEST", search_string!!)


        initToolbar()


        get_data()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val user_type: String
        val admin = "admin"
        try {
            user_type = Utils.get_logged_in_user()!!.user_type
            if (user_type == admin) {
                menu.getItem(0).isVisible = false
                menu.getItem(1).isVisible = false
                menu.getItem(4).isVisible = true
                menu.getItem(5).isVisible = true
                menu.getItem(6).isVisible = true
                menu.getItem(7).isVisible = true
                menu.getItem(8).isVisible = true
            } else {
                menu.getItem(0).isVisible = false
                menu.getItem(1).isVisible = false
                menu.getItem(2).isVisible = true
                menu.getItem(3).isVisible = true
                menu.getItem(4).isVisible = false
                menu.getItem(5).isVisible = false
                menu.getItem(6).isVisible = false
                menu.getItem(7).isVisible = false
                menu.getItem(8).isVisible = false
            }
        } catch (e: Exception) {
            menu.getItem(0).isVisible = true
            menu.getItem(1).isVisible = true
            menu.getItem(2).isVisible = false
            menu.getItem(4).isVisible = false
            menu.getItem(5).isVisible = false
            menu.getItem(6).isVisible = false
            menu.getItem(7).isVisible = false
            menu.getItem(8).isVisible = false
        }
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_cart) {
            val i = Intent(this@SearchResultsActivity, CheckOutActivity::class.java)
            this@SearchResultsActivity.startActivity(i)
        } else if (item.itemId == R.id.action_add_product) {
            val i = Intent(this@SearchResultsActivity, AddItemActivity::class.java)
            this@SearchResultsActivity.startActivity(i)
        } else if (item.itemId == R.id.action_orders) {
            val i = Intent(this@SearchResultsActivity, AdminOrdersActivity::class.java)
            this@SearchResultsActivity.startActivity(i)
        } else if (item.itemId == R.id.action_login) {
            val i = Intent(this@SearchResultsActivity, SignInActivity::class.java)
            this@SearchResultsActivity.startActivity(i)
        } else if (item.itemId == R.id.action_create_acc) {
            val i = Intent(this@SearchResultsActivity, SignUpActivity::class.java)
            this@SearchResultsActivity.startActivity(i)
        } else if (item.itemId == R.id.action_customers) {
            val i = Intent(this@SearchResultsActivity, AdminUsersActivity::class.java)
            this@SearchResultsActivity.startActivity(i)
        } else if (item.itemId == R.id.action_notification) {
            val i = Intent(this@SearchResultsActivity, NotificationActivity::class.java)
            this@SearchResultsActivity.startActivity(i)
        } else if (item.itemId == R.id.action_logout) {
            Toast.makeText(this, "Logging you out....", Toast.LENGTH_SHORT).show()
            try {
                UserModel.deleteAll(UserModel::class.java)
                Toast.makeText(this, "Logged you out successfully!", Toast.LENGTH_LONG).show()
                val i = Intent(this@SearchResultsActivity, MainActivity::class.java)
                this@SearchResultsActivity.startActivity(i)
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Failed to Log you out because " + e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    var products: List<ItemModel> = ArrayList<ItemModel>()
    var db = FirebaseFirestore.getInstance()

    private fun get_data() {
        //db.collection("PRODUCTS").orderBy("product_name").startAt("tv").endAt("tv"+'\uf8ff');
        db.collection("PRODUCTS").get().addOnSuccessListener { queryDocumentSnapshots ->
            for (document in queryDocumentSnapshots) {
                val data = document.getString("product_name")!!.toLowerCase()
                if (data.contains(search_string!!)) {
                    products = Arrays.asList(document.toObject(ItemModel::class.java))
                    initComponents()
                }
            }
        }.addOnFailureListener { initComponents() }
    }

    var progressBar: ProgressBar? = null


    private fun initComponents() {
        recyclerView!!.layoutManager = GridLayoutManager(this, 2)
        //recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 8), true));
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.isNestedScrollingEnabled = false
        progressBar!!.visibility = View.GONE
        recyclerView!!.visibility = View.VISIBLE
        mAdapter = ProductAdapter(products, this, "0")
        recyclerView!!.adapter = mAdapter
        mAdapter!!.SetOnItemClickListener(object : ProductAdapter.OnItemClickListener {
            override fun onItemClick(view: View?, obj: ItemModel?, pos: Int) {
                val i = Intent(this@SearchResultsActivity, ProductActivity::class.java)
                i.putExtra("id", obj?.product_id)
                this@SearchResultsActivity.startActivity(i)
            }
        })
    }

    private fun initToolbar() {
        progressBar = findViewById(R.id.progress_bar)
        recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        recyclerView?.setVisibility(View.GONE)
        home = findViewById(R.id.home_icon)
        search = findViewById(R.id.search_icon)
        shopping = findViewById(R.id.shopping_icon)
        setting = findViewById(R.id.setting_icon)
        user = findViewById(R.id.user_icon)
        home?.setOnClickListener(View.OnClickListener {
            Toast.makeText(this@SearchResultsActivity, "you clicked home", Toast.LENGTH_SHORT)
                .show()
            home?.setBackgroundResource(R.drawable.circle)
            search?.setBackgroundResource(R.drawable.circle_no_bg)
            shopping?.setBackgroundResource(R.drawable.circle_no_bg)
            setting?.setBackgroundResource(R.drawable.circle_no_bg)
            user?.setBackgroundResource(R.drawable.circle_no_bg)
        })
        search?.setOnClickListener(View.OnClickListener {
            val i = Intent(this@SearchResultsActivity, SearchActivity::class.java)
            this@SearchResultsActivity.startActivity(i)
            search?.setBackgroundResource(R.drawable.circle)
            home?.setBackgroundResource(R.drawable.circle_no_bg)
            shopping?.setBackgroundResource(R.drawable.circle_no_bg)
            setting?.setBackgroundResource(R.drawable.circle_no_bg)
            user?.setBackgroundResource(R.drawable.circle_no_bg)
        })
        shopping?.setOnClickListener(View.OnClickListener {
            val i = Intent(this@SearchResultsActivity, CategoryActivity::class.java)
            this@SearchResultsActivity.startActivity(i)
            shopping?.setBackgroundResource(R.drawable.circle)
            home?.setBackgroundResource(R.drawable.circle_no_bg)
            search?.setBackgroundResource(R.drawable.circle_no_bg)
            setting?.setBackgroundResource(R.drawable.circle_no_bg)
            user?.setBackgroundResource(R.drawable.circle_no_bg)
        })
        setting?.setOnClickListener(View.OnClickListener {
            Toast.makeText(this@SearchResultsActivity, "you clicked setting", Toast.LENGTH_SHORT)
                .show()
            setting?.setBackgroundResource(R.drawable.circle)
            home?.setBackgroundResource(R.drawable.circle_no_bg)
            search?.setBackgroundResource(R.drawable.circle_no_bg)
            shopping?.setBackgroundResource(R.drawable.circle_no_bg)
            user?.setBackgroundResource(R.drawable.circle_no_bg)
        })
        user?.setOnClickListener(View.OnClickListener {
            Toast.makeText(this@SearchResultsActivity, "you clicked user", Toast.LENGTH_SHORT)
                .show()
            user?.setBackgroundResource(R.drawable.circle)
            home?.setBackgroundResource(R.drawable.circle_no_bg)
            search?.setBackgroundResource(R.drawable.circle_no_bg)
            shopping?.setBackgroundResource(R.drawable.circle_no_bg)
            setting?.setBackgroundResource(R.drawable.circle_no_bg)
        })
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        //toolbar.setNavigationIcon(R.drawable.ic_menu);
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