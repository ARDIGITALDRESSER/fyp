package com.example.fyp

data class ProductModel(
    val productId: String = "",
    val categoryId: String = "",
    val name: String = "",
    val price: String = "",
    val description: String = "",
    val image: String = "" // ✅ Ensure this stores Cloudinary URL
)

