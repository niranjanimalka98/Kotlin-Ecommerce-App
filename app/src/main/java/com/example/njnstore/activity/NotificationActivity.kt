package com.example.njnstore.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.njnstore.MainActivity
import com.example.njnstore.R
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONException
import org.json.JSONObject

class NotificationActivity : AppCompatActivity() {

    private var mRequestQue: RequestQueue? = null
    private val URL = "https://fcm.googleapis.com/fcm/send"

    var notification_title: EditText? = null
    var notification_body: EditText? = null
    var btn_notification: Button? = null

    var CHANNEL_ID = "1"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        window.statusBarColor = resources.getColor(R.color.light_blue_900)
        if (intent.hasExtra("category")) {
            val intent = Intent(this@NotificationActivity, MainActivity::class.java)
            startActivity(intent)
        }

        notification_title = findViewById(R.id.notification_title)
        notification_body = findViewById(R.id.notification_body)
        btn_notification = findViewById(R.id.btn_notification)
        mRequestQue = Volley.newRequestQueue(this)
        FirebaseMessaging.getInstance().subscribeToTopic("news")

        btn_notification?.setOnClickListener(View.OnClickListener { sendNotification() })


    }

    private fun sendNotification() {
        val json = JSONObject()
        try {
            json.put("to", "/topics/" + "news")
            val notificationObj = JSONObject()
            notificationObj.put("title", notification_title!!.text.toString())
            notificationObj.put("body", notification_body!!.text.toString())
            val extraData = JSONObject()
            extraData.put("brandId", "puma")
            extraData.put("category", "Shoes")
            json.put("notification", notificationObj)
            json.put("data", extraData)
            val request: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST, URL,
                json,
                Response.Listener { Log.d("NJN", "onResponse: ") },
                Response.ErrorListener { error ->
                    Log.d(
                        "NJN",
                        "onError: " + error.networkResponse
                    )
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val header: MutableMap<String, String> = HashMap()
                    header["content-type"] = "application/json"
                    header["authorization"] =
                        "key=AAAAyMrhS60:APA91bHyMVIXNpXEjs-oCqzBCJV5zZWv8YaIDlv82rK_QrTkuDjqR-PuwzNd6LJE4IiODmhGWQp55ROcsHLFKZ8NcYY7mL2E95700ae6iBvDy2MfUJoHRHN-q-cjPqcu4Y5iqIVN6tIG"
                    return header
                }
            }
            mRequestQue!!.add(request)
            Toast.makeText(this, "Sent", Toast.LENGTH_SHORT).show()
            notification_title!!.setText("")
            notification_body!!.setText("")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


}