package com.example.njnstore.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.njnstore.MainActivity
import com.example.njnstore.R
import com.example.njnstore.activity.SignUpActivity.Companion.USERS_TABLE
import com.example.njnstore.model.UserModel
import com.example.njnstore.tools.Utils
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        context = this

        bind_views()

        val loggedInUser = Utils.get_logged_in_user()
        if (loggedInUser != null) {
            Toast.makeText(this, "You are logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
    }

    var email: EditText? = null
    var password:EditText? = null
    var register: TextView? = null
    var sign_in: Button? = null
    var db = FirebaseFirestore.getInstance()
    private fun bind_views() {
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        sign_in = findViewById(R.id.sign_in_button)
        register = findViewById(R.id.register)
        with(sign_in) { this?.setOnClickListener(View.OnClickListener { sign_user() }) }
        with(register) {
            this?.setOnClickListener(View.OnClickListener {
                val intent = Intent(context, SignUpActivity::class.java)
                context!!.startActivity(intent)
            })
        }
    }

    var email_value = ""
    var password_value = ""
    var progressDialog: ProgressDialog? = null
    var context: Context? = null
    private fun sign_user() {
        email_value = email!!.text.toString().trim { it <= ' ' }
        password_value = password?.getText().toString().trim { it <= ' ' }
        if (email_value.isEmpty() || password_value.isEmpty()) {
            Toast.makeText(this, "Fill required fields", Toast.LENGTH_SHORT).show()
            return
        }
        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Please Wait")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
        db.collection(USERS_TABLE).whereEqualTo("email", email_value)
            .get()
            .addOnSuccessListener(OnSuccessListener { queryDocumentSnapshots ->
                if (queryDocumentSnapshots == null) {
                    progressDialog!!.hide()
                    progressDialog!!.dismiss()
                    Toast.makeText(context, "Email not found on our database", Toast.LENGTH_LONG)
                        .show()
                    return@OnSuccessListener
                }
                if (queryDocumentSnapshots.isEmpty) {
                    progressDialog!!.hide()
                    progressDialog!!.dismiss()
                    Toast.makeText(context, "Email not found on our database", Toast.LENGTH_LONG)
                        .show()
                    return@OnSuccessListener
                }
                val users = queryDocumentSnapshots.toObjects(
                    UserModel::class.java
                )
                if (!users[0].password.equals(password_value)) {
                    progressDialog!!.hide()
                    progressDialog!!.dismiss()
                    Toast.makeText(context, "Wrong password", Toast.LENGTH_LONG).show()
                    return@OnSuccessListener
                }
                progressDialog!!.hide()
                progressDialog!!.dismiss()
                if (login_user(users[0])) {
                    Toast.makeText(context, "Your login successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context!!.startActivity(intent)
                    return@OnSuccessListener
                } else {
                    Toast.makeText(context, "Failed to login", Toast.LENGTH_SHORT).show()
                }
            }).addOnFailureListener {
                Toast.makeText(
                    this@SignInActivity,
                    "Failed to create an account",
                    Toast.LENGTH_SHORT
                )
                    .show()
                progressDialog!!.hide()
                progressDialog!!.dismiss()
            }
    }

    private fun login_user(u: UserModel): Boolean {
        return try {
            UserModel.save(u)
            true
        } catch (e: Exception) {
            false
        }
    }
}