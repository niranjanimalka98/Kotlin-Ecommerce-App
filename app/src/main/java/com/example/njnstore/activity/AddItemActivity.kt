package com.example.njnstore.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.njnstore.MainActivity
import com.example.njnstore.R
import com.example.njnstore.model.ItemModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class AddItemActivity : AppCompatActivity() {
    var btn_done: ImageButton? = null
    var product_photo: ImageView? = null
    var product_name: TextInputEditText? = null
    var product_details: TextInputEditText? = null
    var product_price: TextInputEditText? = null
    var quantity: TextInputEditText? = null
    private val PICK_IMAGE_REQUEST = 1
    val Product_Table = "PRODUCTS"
    var Category_view: TextInputEditText? = null
    var itemModel: ItemModel = ItemModel()
    var db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.light_blue_900)))
        window.statusBarColor = resources.getColor(R.color.light_blue_900)
        bind_Views()
    }

    private fun bind_Views() {
        itemModel.product_id = db.collection(Product_Table).document().id
        btn_done = findViewById(R.id.btn_done)
        product_name = findViewById(R.id.product_name)
        product_details = findViewById(R.id.product_details)
        product_price = findViewById(R.id.product_price)
        quantity = findViewById(R.id.product_quantity)
        product_photo = findViewById(R.id.product_photo)
        progressDialog = ProgressDialog(this)
        Category_view = findViewById(R.id.Category_view)
        with(Category_view) { this?.setOnClickListener(View.OnClickListener { selectCategory() }) }
        with(product_photo) { this?.setOnClickListener(View.OnClickListener { chooseImage() }) }
        with(btn_done) { this?.setOnClickListener(View.OnClickListener { submit_product() }) }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Product Image"),
            PICK_IMAGE_REQUEST
        )
    }

    var selected_Category = 0
    private fun selectCategory() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Category")
        builder.setSingleChoiceItems(
            categories, selected_Category
        ) { dialogInterface, i ->
            Category_view!!.setText(categories[i])
            selected_Category = i
        }

//        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Category_view.setText(categories[i]);
//            }
//        });
        builder.setNegativeButton("Ok", null)
        builder.show()
    }

    private var imagePath: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imagePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imagePath)
                product_photo!!.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    var progressDialog: ProgressDialog? = null
    var ref_main: StorageReference? = null
    private fun send_to_firestore() {
        Toast.makeText(this, "Submitting...", Toast.LENGTH_SHORT).show()
        db.collection(Product_Table).document(itemModel.product_id).set(itemModel)
            .addOnSuccessListener(
                OnSuccessListener {
                    progressDialog!!.hide()
                    progressDialog!!.dismiss()
                    Toast.makeText(this@AddItemActivity, "Uploading Successful", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                    return@OnSuccessListener
                }).addOnFailureListener(OnFailureListener { e ->
                progressDialog!!.hide()
                Toast.makeText(this@AddItemActivity, "Uploading Failed " + e.message, Toast.LENGTH_LONG)
                    .show()
                return@OnFailureListener
            })
    }

    private fun submit_product() {
        itemModel.product_name = product_name!!.text.toString()
        if (itemModel.product_name.isEmpty()) {
            Toast.makeText(this, "Product Name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        itemModel.category = Category_view!!.text.toString()
        if (itemModel.category.isEmpty()) {
            Toast.makeText(this, "Product Category cannot be empty", Toast.LENGTH_SHORT).show()
            selectCategory()
            return
        }
        if (product_price!!.text.toString().isEmpty()) {
            Toast.makeText(this, "Product Price cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            itemModel.price = Integer.valueOf(product_price!!.text.toString())
        } catch (e: Exception) {
        }
        if (itemModel.price < 0) {
            Toast.makeText(this, "Product Price should be grater than zero", Toast.LENGTH_SHORT)
                .show()
            return
        }
        // Start Get and Set Quantity
        if (quantity!!.text.toString().isEmpty()) {
            Toast.makeText(this, "Product Quantity cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            itemModel.quantity = Integer.valueOf(quantity!!.text.toString())
        } catch (e: Exception) {
        }
        if (itemModel.quantity < 0) {
            Toast.makeText(this, "Product Quantity should be grater than zero", Toast.LENGTH_SHORT)
                .show()
            return
        }
        // End Get and Set Quantity
        itemModel.description = product_details!!.text.toString()
        if (imagePath == null) {
            Toast.makeText(this, "Select Image", Toast.LENGTH_SHORT).show()
            return
        }
        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Uploading...")
        progressDialog!!.show()
        ref_main = FirebaseStorage.getInstance().reference
        ref_main!!.child("products/" + itemModel.product_id).putFile(imagePath!!)
            .addOnSuccessListener(
                OnSuccessListener {
                    Toast.makeText(
                        this@AddItemActivity,
                        "Uploaded Successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    ref_main!!.child("products/" + itemModel.product_id).downloadUrl.addOnSuccessListener(
                        OnSuccessListener { uri ->
                            itemModel.photo = uri.toString()
                            send_to_firestore()
                            return@OnSuccessListener
                        }).addOnFailureListener(OnFailureListener {
                        itemModel.photo =
                            "https://images.unsplash.com/photo-1578328819058-b69f3a3b0f6b?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=774&q=80"
                        send_to_firestore()
                        return@OnFailureListener
                    })
                }).addOnFailureListener(OnFailureListener { e ->
                Toast.makeText(
                    this@AddItemActivity,
                    "Failed to uplod photo" + e.message,
                    Toast.LENGTH_SHORT
                ).show()
                itemModel.photo =
                    "https://images.unsplash.com/photo-1578328819058-b69f3a3b0f6b?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=774&q=80"
                send_to_firestore()
                return@OnFailureListener
            })
    }

    fun back(view: View?) {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

    companion object {
        val categories = arrayOf(
            "None", "Electronic", "Food", "School items", "Women", "Men", "Kids"
        )
    }
}