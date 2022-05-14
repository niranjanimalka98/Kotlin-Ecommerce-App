package com.example.njnstore.activity

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.njnstore.MainActivity
import com.example.njnstore.R
import com.example.njnstore.model.UserModel
import com.example.njnstore.tools.Utils
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.*


class SignUpActivity : AppCompatActivity() {
    var loggedInUser: UserModel? = null
    var db = FirebaseFirestore.getInstance()
    private val PICK_IMAGE_REQUEST = 1
    var profile_photo: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        bindViews()
        loggedInUser = Utils.get_logged_in_user()
        if (loggedInUser != null) {
            Toast.makeText(this, "You are logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
    }

    var first_name: EditText? = null
    var last_name: EditText? = null
    var email: EditText? = null
    var password: EditText? = null
    var phone_number: EditText? = null
    var address: EditText? = null
    var sign_up_btn: Button? = null
    private fun bindViews() {
        userModel.user_id = db.collection(USERS_TABLE).document().id
        first_name = findViewById(R.id.first_name)
        last_name = findViewById<EditText>(R.id.last_name)
        email = findViewById<EditText>(R.id.email)
        password = findViewById<EditText>(R.id.password)
        phone_number = findViewById<EditText>(R.id.phone_number)
        address = findViewById<EditText>(R.id.address)
        sign_up_btn = findViewById(R.id.sign_up_button)
        profile_photo = findViewById(R.id.profile_photo)
        with(sign_up_btn) { this?.setOnClickListener(View.OnClickListener { validate_data() }) }

        with(profile_photo) { this?.setOnClickListener(View.OnClickListener { chooseImage() }) }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select profile Image"),
            PICK_IMAGE_REQUEST
        )
    }

    private var imagePath: Uri? = null
    var ref_main: StorageReference? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imagePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imagePath)
                profile_photo?.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    var userModel = UserModel()
    private fun validate_data() { // sign up activity validation part
        userModel.first_name = first_name!!.text.toString()
        if (userModel.first_name.isEmpty()) {
            Toast.makeText(this, "First Name cannot be empty", Toast.LENGTH_SHORT).show()
            first_name!!.requestFocus()
            return
        }
        if (userModel.first_name.length < 2) {
            Toast.makeText(this, "First Name too short", Toast.LENGTH_SHORT).show()
            first_name!!.requestFocus()
            return
        }
        userModel.last_name = last_name?.getText().toString()
        if (userModel.last_name.isEmpty()) {
            Toast.makeText(this, "Last Name cannot be empty", Toast.LENGTH_SHORT).show()
            last_name?.requestFocus()
            return
        }
        if (userModel.last_name.length < 2) {
            Toast.makeText(this, "Last Name too short", Toast.LENGTH_SHORT).show()
            last_name?.requestFocus()
            return
        }
        userModel.email = email?.getText().toString()
        if (userModel.email.isEmpty()) {
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show()
            email?.requestFocus()
            return
        }
        if (userModel.email.length < 5) {
            Toast.makeText(this, "Email too short", Toast.LENGTH_SHORT).show()
            email?.requestFocus()
            return
        }
        userModel.password = password?.getText().toString()
        if (userModel.password.isEmpty()) {
            Toast.makeText(this, "Last Name cannot be empty", Toast.LENGTH_SHORT).show()
            password?.requestFocus()
            return
        }
        if (userModel.password.length < 8) {
            Toast.makeText(this, "Password too weak", Toast.LENGTH_SHORT).show()
            password?.requestFocus()
            return
        }
        userModel.address = address?.getText().toString()
        userModel.phone_number = phone_number?.getText().toString()
        userModel.user_type = "customer"
        userModel.profile_photo = ""

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Please Wait")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()

        ref_main = FirebaseStorage.getInstance().reference
        ref_main!!.child("customers/" + userModel.user_id).putFile(imagePath!!)
            .addOnSuccessListener(
                OnSuccessListener {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Uploaded Successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    ref_main!!.child("customers/" + userModel.user_id).downloadUrl.addOnSuccessListener(
                        OnSuccessListener { uri ->
                            userModel.profile_photo = uri.toString()
                            progressDialog!!.hide()
                            progressDialog!!.dismiss()
                            submit_data()
                            return@OnSuccessListener
                        }).addOnFailureListener(OnFailureListener {
                        userModel.profile_photo =
                            "https://images.unsplash.com/photo-1578328819058-b69f3a3b0f6b?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=774&q=80"
                        progressDialog!!.hide()
                        progressDialog!!.dismiss()
                        submit_data()
                        return@OnFailureListener
                    })
                }).addOnFailureListener(OnFailureListener { e ->
                Toast.makeText(
                    this@SignUpActivity,
                    "Failed to uplod photo" + e.message,
                    Toast.LENGTH_SHORT
                ).show()
                userModel.profile_photo =
                    "https://images.unsplash.com/photo-1578328819058-b69f3a3b0f6b?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=774&q=80"
                progressDialog!!.hide()
                progressDialog!!.dismiss()
                submit_data()
                return@OnFailureListener
            })

    }

    var progressDialog: ProgressDialog? = null

    companion object {
        val USERS_TABLE = "users"
    }

    private fun submit_data() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Please Wait")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
        db.collection(USERS_TABLE).whereEqualTo("email", userModel.email).get()
            .addOnSuccessListener(
                OnSuccessListener { queryDocumentSnapshots ->
                    if (!queryDocumentSnapshots.isEmpty) {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Email already exist",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressDialog!!.hide()
                        progressDialog!!.dismiss()
                        return@OnSuccessListener
                    }
                    userModel.user_id = db.collection(USERS_TABLE).document().id
                    userModel.reg_date = Calendar.getInstance().timeInMillis.toString() + ""
                    db.collection(USERS_TABLE).document(userModel.user_id).set(userModel)
                        .addOnSuccessListener(
                            OnSuccessListener {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    "User Account Created Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                progressDialog!!.hide()
                                progressDialog!!.dismiss()
                                if (login_user()) {
                                    Toast.makeText(
                                        this@SignUpActivity,
                                        "Your login successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent =
                                        Intent(this@SignUpActivity, MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    this@SignUpActivity.startActivity(intent)
                                    return@OnSuccessListener
                                } else {
                                    Toast.makeText(
                                        this@SignUpActivity,
                                        "Failed to login",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }).addOnFailureListener {
                            Toast.makeText(
                                this@SignUpActivity,
                                "Failed to create an account",
                                Toast.LENGTH_SHORT
                            ).show()
                            progressDialog!!.hide()
                            progressDialog!!.dismiss()
                        }
                }).addOnFailureListener {
                Toast.makeText(
                    this@SignUpActivity,
                    "Failed to create an account",
                    Toast.LENGTH_SHORT
                )
                    .show()
                progressDialog!!.hide()
                progressDialog!!.dismiss()
            }
    }

    private val TAG = "SignUp_Activity"

    private fun login_user(): Boolean {
        return try {
            UserModel.save(userModel)
            true
        } catch (e: Exception) {
            false
        }
    }
}