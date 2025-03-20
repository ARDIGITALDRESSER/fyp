package com.example.fyp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddToCartBottomSheet(
    private val productId: String,
    private val categoryId: String,
    private val imageUrl: String,
    private val productName: String,
    private val productPrice: String,
    private val availableSizes: List<String>
) : BottomSheetDialogFragment() {

    private lateinit var database: DatabaseReference
    private var selectedSize: String = availableSizes.firstOrNull() ?: "S"
    private var quantity = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottom_sheet_add_to_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageViewProduct: ImageView = view.findViewById(R.id.ivProductImage)
        val textViewProductName: TextView = view.findViewById(R.id.tvProductName)
        val textViewProductPrice: TextView = view.findViewById(R.id.tvProductPrice)
        val sizesChipGroup: ChipGroup = view.findViewById(R.id.sizesChipGroup)
        val btnIncrease: ImageButton = view.findViewById(R.id.btnIncrease)
        val btnDecrease: ImageButton = view.findViewById(R.id.btnDecrease)
        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
        val btnAddToCart: Button = view.findViewById(R.id.btnAddToCart)

        database = FirebaseDatabase.getInstance().reference

        // Set product details
        Glide.with(requireContext()).load(imageUrl).into(imageViewProduct)
        textViewProductName.text = productName
        textViewProductPrice.text = "PKR $productPrice"

        // Populate size selection
        sizesChipGroup.removeAllViews()
        availableSizes.forEach { size ->
            val chip = Chip(requireContext()).apply {
                text = size
                isCheckable = true
                isClickable = true
            }
            sizesChipGroup.addView(chip)
            if (size == selectedSize) chip.isChecked = true
        }

        sizesChipGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedChip = view.findViewById<Chip>(checkedId)
            selectedSize = selectedChip?.text?.toString() ?: availableSizes.first()
        }

        btnIncrease.setOnClickListener {
            quantity++
            tvQuantity.text = quantity.toString()
        }

        btnDecrease.setOnClickListener {
            if (quantity > 1) {
                quantity--
                tvQuantity.text = quantity.toString()
            }
        }

        btnAddToCart.setOnClickListener {
            addToCart()
        }
    }

    private fun addToCart() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val cartRef = database.child("users").child(userId).child("cart")
        val cartItem = mapOf(
            "productId" to productId,
            "productName" to productName,
            "productPrice" to productPrice,
            "selectedSize" to selectedSize,
            "quantity" to quantity
        )

        cartRef.child(productId).setValue(cartItem).addOnSuccessListener {
            Toast.makeText(context, "Added to cart!", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }
}
