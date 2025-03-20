package com.example.fyp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.*

class AdminProductDetailsActivity : AppCompatActivity() {

    private lateinit var productImageView: ImageView
    private lateinit var editTextProductName: EditText
    private lateinit var editTextProductPrice: EditText
    private lateinit var editTextProductDescription: EditText
    private lateinit var btnDeleteProduct: Button

    private lateinit var database: DatabaseReference
    private var productId: String? = null
    private var categoryId: String? = null // ✅ Needed to get the correct product path

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_product_details)

        productImageView = findViewById(R.id.productImageView)
        editTextProductName = findViewById(R.id.editTextProductName)
        editTextProductPrice = findViewById(R.id.editTextProductPrice)
        editTextProductDescription = findViewById(R.id.editTextProductDescription)
        btnDeleteProduct = findViewById(R.id.btnDeleteProduct)

        productId = intent.getStringExtra("productId")
        categoryId = intent.getStringExtra("categoryId") // ✅ Get categoryId from intent

        if (categoryId.isNullOrEmpty() || productId.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid product data", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        database = FirebaseDatabase.getInstance().getReference("products").child(categoryId!!)

        loadProductDetails() // ✅ Load product details when activity starts

        btnDeleteProduct.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun loadProductDetails() {
        database.child(productId!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val product = snapshot.getValue(ProductModel::class.java)
                    product?.let {
                        editTextProductName.setText(it.name)
                        editTextProductPrice.setText(it.price)
                        editTextProductDescription.setText(it.description)

                        // ✅ Load Image using Glide
                        Glide.with(this@AdminProductDetailsActivity)
                            .load(it.image) // ✅ Make sure `image` field in Firebase has Cloudinary URL
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.placeholder_image)
                            .into(productImageView)
                    }
                } else {
                    Toast.makeText(this@AdminProductDetailsActivity, "Product not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminProductDetailsActivity, "Failed to load product", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete this product?")
            .setPositiveButton("Yes") { _, _ -> deleteProduct() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteProduct() {
        productId?.let { id ->
            database.child(id).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this, "Product deleted successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to delete product", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
