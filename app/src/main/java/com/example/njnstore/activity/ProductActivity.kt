package com.example.njnstore.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.njnstore.R
import com.example.njnstore.model.CartModel
import com.example.njnstore.model.ItemModel
import com.example.njnstore.model.UserModel
import com.example.njnstore.tools.Utils
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore

class ProductActivity : AppCompatActivity() {
    var loggedInUser: UserModel? = null
    var id: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        initToolbar()
        window.statusBarColor = resources.getColor(R.color.light_blue_900)
        window.navigationBarColor = resources.getColor(R.color.light_blue_900)

        context = this

        val i = intent
        id = i.getStringExtra("id")

        if (id == null || id!!.length < 1) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        bind_views()
        get_data()
    }

    var product_image: ImageView? = null
    var product_name: TextView? = null
    var product_price: TextView? = null
    var product_details: TextView? = null
    var product_qty: TextView? = null
    var add_to_cart: Button? = null
    var quantity: TextView? = null
    private fun bind_views() {
        product_image = findViewById(R.id.product_image)
        product_name = findViewById(R.id.product_name)
        product_price = findViewById<TextView>(R.id.product_price)
        product_details = findViewById<TextView>(R.id.product_details)
        product_qty = findViewById<TextView>(R.id.product_qty)
        add_to_cart = findViewById(R.id.add_to_cart)
        quantity = findViewById(R.id.quantity)
    }

    private fun feed_data() {
        product_image?.let {
            Glide.with(context!!)
                .load(product?.photo)
                .into(it)
        }
        product_name?.setText(product?.product_name)
        product_price?.setText(product?.price.toString() + "")
        product_details?.setText(product?.description.toString() + "")
        add_to_cart!!.setOnClickListener(View.OnClickListener {
            loggedInUser = Utils.get_logged_in_user()
            if (loggedInUser == null) {
                Toast.makeText(this@ProductActivity, "You are not logged in", Toast.LENGTH_SHORT)
                    .show()
                val i = Intent(this@ProductActivity, SignInActivity::class.java)
                this@ProductActivity.startActivity(i)
                finish()
                return@OnClickListener
            } else {
                add_product_to_cart()
            }
        })
    }

    private fun add_product_to_cart() {
        val cartModel = CartModel()
        cartModel.product_id = product!!.product_id
        cartModel.product_name = product!!.product_name
        cartModel.product_price = product!!.price * Integer.valueOf(quantity?.getText().toString())
        cartModel.quantity = Integer.valueOf(quantity!!.text.toString())
        cartModel.product_photo = product!!.photo
        try {
            //cartModel.save();
            CartModel.save(cartModel)
            Toast.makeText(context, "Product added to the cart", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to save " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    var product: ItemModel? = ItemModel()
    var db = FirebaseFirestore.getInstance()
    var context: Context? = null
    private fun get_data() {
        db.collection("PRODUCTS").document(id!!).get()
            .addOnSuccessListener(OnSuccessListener { documentSnapshot ->
                if (!documentSnapshot.exists()) {
                    Toast.makeText(context, "Product not found", Toast.LENGTH_SHORT).show()
                    finish()
                    return@OnSuccessListener
                }
                product = documentSnapshot.toObject(ItemModel::class.java)
                feed_data()
            }).addOnFailureListener(OnFailureListener { e ->
                Toast.makeText(context, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                finish()
                return@OnFailureListener
            })
    }

    private fun initToolbar() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Product Details")
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

    var minteger = 1
    fun increaseInteger(view: View?) {
        minteger = minteger + 1
        display(minteger)
    }

    fun decreaseInteger(view: View?) {
        minteger = minteger - 1
        display(minteger)
    }

    private fun display(number: Int) {
        val displayInteger = findViewById<View>(
            R.id.quantity
        ) as TextView
        displayInteger.text = "" + number
        if (number < 1) {
            minteger = 1
            displayInteger.text = "" + 1
        }
    }
}