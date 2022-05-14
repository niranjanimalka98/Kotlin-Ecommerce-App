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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.njnstore.R
import com.example.njnstore.adapter.UserAdapter
import com.example.njnstore.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore

class AdminUsersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_users)
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

    var users: List<UserModel> = ArrayList<UserModel>()
    var db = FirebaseFirestore.getInstance()
    private fun get_data() {
        db.collection("users").get().addOnSuccessListener { queryDocumentSnapshots ->
            users = queryDocumentSnapshots.toObjects(UserModel::class.java)
            initComponents()
        }.addOnFailureListener { initComponents() }
    }

    private var mAdapter: UserAdapter? = null
    private fun initComponents() {
        recyclerView!!.layoutManager = GridLayoutManager(this, 2)
        //recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 8), true));
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.isNestedScrollingEnabled = false
        progressBar!!.visibility = View.GONE
        recyclerView!!.visibility = View.VISIBLE
        mAdapter = UserAdapter(users, this, "0")
        recyclerView!!.adapter = mAdapter
        mAdapter!!.SetOnItemClickListener(object : UserAdapter.OnItemClickListener {
            override fun onItemClick(view: View?, obj: UserModel?, pos: Int) {
                val i = Intent(this@AdminUsersActivity, AdminUserActivity::class.java)
                i.putExtra("user_id", obj!!.user_id)
                this@AdminUsersActivity.startActivity(i)
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