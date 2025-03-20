package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.auth.FirebaseAuth

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var imageViewProduct: ImageView
    private lateinit var textViewProductName: TextView
    private lateinit var textViewProductPrice: TextView
    private lateinit var textViewProductDescription: TextView
    private lateinit var buttonTryOn: Button
    private lateinit var buttonBuyNow: Button
    private lateinit var buttonAddToCart: Button
    private lateinit var sizesChipGroup: ChipGroup

    private var selectedSize: String = "S"
    private var productId: String? = null
    private var categoryId: String? = null

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        // Initialize UI elements
        imageViewProduct = findViewById(R.id.imageViewProduct)
        textViewProductName = findViewById(R.id.textViewProductName)
        textViewProductPrice = findViewById(R.id.textViewProductPrice)
        textViewProductDescription = findViewById(R.id.textViewProductDescription)
        buttonTryOn = findViewById(R.id.buttonTryOn)
        buttonBuyNow = findViewById(R.id.buttonBuyNow)
        buttonAddToCart = findViewById(R.id.buttonAddToCart)
        sizesChipGroup = findViewById(R.id.sizesChipGroup)

        // Get data from intent
        productId = intent.getStringExtra("productId")
        categoryId = intent.getStringExtra("categoryId")

        if (productId.isNullOrEmpty() || categoryId.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid product data", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize Firebase Database Reference
        database = FirebaseDatabase.getInstance().getReference("products").child(categoryId!!)

        loadProductDetails()

        // Button Listeners
        buttonTryOn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        buttonBuyNow.setOnClickListener {
            val priceText = textViewProductPrice.text?.toString() ?: ""

            Log.d("ProductDetailsActivity", "Price before cleaning: $priceText")

            if (priceText.isEmpty()) {
                Log.e("ProductDetailsActivity", "Error: Product price is empty!")
                Toast.makeText(this, "Error: Product price not available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val numericPrice = priceText.replace("PKR ", "").trim()

            Log.d("ProductDetailsActivity", "Numeric Price: $numericPrice")

            val intent = Intent(this, DeliveryActivity::class.java).apply {
                putExtra("productName", textViewProductName.text.toString())
                putExtra("productPrice", numericPrice)
                putExtra("selectedSize", selectedSize)
            }

            startActivity(intent)
        }
    }

    private fun loadProductDetails() {
        database.child(productId!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(applicationContext, "Product not found", Toast.LENGTH_SHORT).show()
                    finish()
                    return
                }

                val productName = snapshot.child("name").getValue(String::class.java) ?: "Unknown"
                val productPrice = snapshot.child("price").getValue(String::class.java) ?: "0"
                val productImage = snapshot.child("image").getValue(String::class.java) ?: ""
                val productDescription = snapshot.child("description").getValue(String::class.java) ?: "No description available"

                val availableSizes = snapshot.child("sizes").children.mapNotNull { it.getValue(String::class.java) }

                // Set UI elements
                textViewProductName.text = productName
                textViewProductPrice.text = "PKR $productPrice"
                textViewProductDescription.text = productDescription

                Glide.with(applicationContext).load(productImage).into(imageViewProduct)

                // Populate sizes dynamically
                sizesChipGroup.removeAllViews()
                availableSizes.forEach { size ->
                    val chip = Chip(this@ProductDetailsActivity).apply {
                        text = size
                        isCheckable = true
                        isClickable = true
                    }
                    sizesChipGroup.addView(chip)
                }

                // Set listener for Add to Cart button
                buttonAddToCart.setOnClickListener {
                    val bottomSheet = AddToCartBottomSheet(
                        productId!!,
                        categoryId!!,
                        productImage,
                        productName,
                        productPrice,
                        availableSizes
                    )
                    bottomSheet.show(supportFragmentManager, "AddToCartBottomSheet")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Failed to load product details", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
