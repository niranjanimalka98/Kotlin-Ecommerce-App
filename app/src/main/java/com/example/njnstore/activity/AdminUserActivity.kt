package com.example.njnstore.activity

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.njnstore.R
import com.example.njnstore.model.UserModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

class AdminUserActivity : AppCompatActivity() {
    var user_image: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_user)
        context = this
        user_id = intent.getStringExtra("user_id")
        if (user_id == null) {
            Toast.makeText(context, "User id not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        initToolbar()
        get_data()
    }

    var user_id: String? = null
    var context: Context? = null

    var db = FirebaseFirestore.getInstance()
    var userModel: UserModel? = null
    var user_name: TextView? = null
    var user_ID:TextView? = null
    var user_email:TextView? = null
    var user_address:TextView? = null
    var user_phone:TextView? = null
    var user_reg:TextView? = null


    private fun get_data() {
        db.collection("users").document(user_id!!).get()
            .addOnSuccessListener(OnSuccessListener { documentSnapshot ->
                if (!documentSnapshot.exists()) {
                    Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                    finish()
                    return@OnSuccessListener
                }
                userModel = documentSnapshot.toObject(UserModel::class.java)
                //feed_data();

                user_image = findViewById(R.id.user_image)
                user_name = findViewById(R.id.user_name)
                user_ID = findViewById(R.id.users_id)
                user_email = findViewById<TextView>(R.id.user_email)
                user_address = findViewById<TextView>(R.id.user_address)
                user_phone = findViewById<TextView>(R.id.user_phone)
                user_reg = findViewById<TextView>(R.id.user_reg)
                user_name?.setText(userModel?.first_name.toString() + " " + userModel?.last_name)
                Glide.with(context!!).load(userModel?.profile_photo).into(user_image!!)
                with(user_ID) { this?.setText(userModel?.user_id) }
                with(user_email) { this?.setText(userModel?.email) }
                with(user_address) { this?.setText(userModel?.address) }
                with(user_phone) { this?.setText(userModel?.phone_number) }
                val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
                val reg_date = simpleDateFormat.format(java.lang.Long.valueOf(userModel?.reg_date))
                with(user_reg) {
                    this?.setText(reg_date.toString())
                }
            }).addOnFailureListener(OnFailureListener { e ->
                Toast.makeText(context, "Failed to get orders " + e.message, Toast.LENGTH_SHORT).show()
                finish()
                return@OnFailureListener
            })
    }

    private fun initToolbar() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Customer Info")
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