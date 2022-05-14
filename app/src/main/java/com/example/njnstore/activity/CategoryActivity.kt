package com.example.njnstore.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.njnstore.R

class CategoryActivity : AppCompatActivity() {
    var electronic: ImageView? = null
    var men: ImageView? = null
    var women: ImageView? = null
    var smart_phones: ImageView? = null
    var school_items: ImageView? = null
    var kids: ImageView? = null
    var grocery: ImageView? = null
    var pharmacy: ImageView? = null
    var watches: ImageView? = null
    var beauty: ImageView? = null
    var shoes: ImageView? = null
    var kitchen: ImageView? = null
    var sport: ImageView? = null
    var furniture: ImageView? = null
    var jewelry: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        window.statusBarColor = resources.getColor(android.R.color.white)

        onImageClick()
    }

    private fun onImageClick() {
        electronic = findViewById<ImageView>(R.id.electronic)
        men = findViewById<ImageView>(R.id.men)
        women = findViewById<ImageView>(R.id.women)
        smart_phones = findViewById<ImageView>(R.id.smart_phones)
        school_items = findViewById<ImageView>(R.id.school_items)
        kids = findViewById<ImageView>(R.id.kids)
        grocery = findViewById<ImageView>(R.id.grocery)
        pharmacy = findViewById<ImageView>(R.id.pharmacy)
        watches = findViewById<ImageView>(R.id.watches)
        beauty = findViewById<ImageView>(R.id.beauty)
        shoes = findViewById<ImageView>(R.id.shoes)
        kitchen = findViewById<ImageView>(R.id.kitchen)
        sport = findViewById<ImageView>(R.id.sport)
        furniture = findViewById<ImageView>(R.id.furniture)
        jewelry = findViewById<ImageView>(R.id.jewelry)

        //image on click
        electronic?.setOnClickListener(View.OnClickListener {
            val i = Intent(this@CategoryActivity, CategoryResultsActivity::class.java)
            i.putExtra("category", "Electronic")
            this@CategoryActivity.startActivity(i)
        })
        men?.setOnClickListener(View.OnClickListener {
            val i = Intent(this@CategoryActivity, CategoryResultsActivity::class.java)
            i.putExtra("category", "Men")
            this@CategoryActivity.startActivity(i)
        })
        women?.setOnClickListener(View.OnClickListener {
            val i = Intent(this@CategoryActivity, CategoryResultsActivity::class.java)
            i.putExtra("category", "Women")
            this@CategoryActivity.startActivity(i)
        })
        smart_phones?.setOnClickListener(View.OnClickListener {
            val i = Intent(this@CategoryActivity, CategoryResultsActivity::class.java)
            i.putExtra("category", "Smart phones")
            this@CategoryActivity.startActivity(i)
        })
        school_items?.setOnClickListener(View.OnClickListener {
            val i = Intent(this@CategoryActivity, CategoryResultsActivity::class.java)
            i.putExtra("category", "School items")
            this@CategoryActivity.startActivity(i)
        })
        kids?.setOnClickListener(View.OnClickListener {
            val i = Intent(this@CategoryActivity, CategoryResultsActivity::class.java)
            i.putExtra("category", "Kids")
            this@CategoryActivity.startActivity(i)
        })
        grocery?.setOnClickListener(View.OnClickListener {
            val i = Intent(this@CategoryActivity, CategoryResultsActivity::class.java)
            i.putExtra("category", "Grocery")
            this@CategoryActivity.startActivity(i)
        })
        pharmacy?.setOnClickListener(View.OnClickListener {
            val i = Intent(this@CategoryActivity, CategoryResultsActivity::class.java)
            i.putExtra("category", "Pharmacy")
            this@CategoryActivity.startActivity(i)
        })
        watches?.setOnClickListener(View.OnClickListener {
            val i = Intent(this@CategoryActivity, CategoryResultsActivity::class.java)
            i.putExtra("category", "Watches")
            this@CategoryActivity.startActivity(i)
        })
        beauty?.setOnClickListener(View.OnClickListener {
            val i = Intent(this@CategoryActivity, CategoryResultsActivity::class.java)
            i.putExtra("category", "Beauty")
            this@CategoryActivity.startActivity(i)
        })
        shoes?.setOnClickListener(View.OnClickListener {
            val i = Intent(this@CategoryActivity, CategoryResultsActivity::class.java)
            i.putExtra("category", "Shoes")
            this@CategoryActivity.startActivity(i)
        })
        kitchen?.setOnClickListener(View.OnClickListener {
            val i = Intent(this@CategoryActivity, CategoryResultsActivity::class.java)
            i.putExtra("category", "Kitchen item")
            this@CategoryActivity.startActivity(i)
        })
        sport?.setOnClickListener(View.OnClickListener {
            val i = Intent(this@CategoryActivity, CategoryResultsActivity::class.java)
            i.putExtra("category", "Sport item")
            this@CategoryActivity.startActivity(i)
        })
        furniture?.setOnClickListener(View.OnClickListener {
            val i = Intent(this@CategoryActivity, CategoryResultsActivity::class.java)
            i.putExtra("category", "Furniture")
            this@CategoryActivity.startActivity(i)
        })
        jewelry?.setOnClickListener(View.OnClickListener {
            val i = Intent(this@CategoryActivity, CategoryResultsActivity::class.java)
            i.putExtra("category", "Jewelry")
            this@CategoryActivity.startActivity(i)
        })
    }
}