package com.example.njnstore.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.njnstore.R

class SearchActivity : AppCompatActivity() {

    var search_btn: ImageView? = null
    var search_text: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        window.statusBarColor = resources.getColor(android.R.color.white)

        search_btn = findViewById(R.id.search_btn)
        search_text = findViewById(R.id.search_text)




        search_btn?.setOnClickListener(View.OnClickListener {
            val i = Intent(this@SearchActivity, SearchResultsActivity::class.java)
            i.putExtra("search_id", search_text?.getText().toString())
            this@SearchActivity.startActivity(i)
        })

    }
}