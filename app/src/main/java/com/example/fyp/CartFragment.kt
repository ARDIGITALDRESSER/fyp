package com.example.fyp

import android.os.Bundle
import android.util.SparseBooleanArray
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CartFragment : Fragment() {

    private lateinit var recyclerViewCart: RecyclerView
    private lateinit var emptyCartLayout: LinearLayout
    private lateinit var startShoppingButton: Button
    private lateinit var placeOrderButton: FloatingActionButton
    private lateinit var cartAdapter: CartAdapter

    private val cartViewModel: CartViewModel by activityViewModels()
    private var actionMode: ActionMode? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        recyclerViewCart = view.findViewById(R.id.recyclerViewCart)
        emptyCartLayout = view.findViewById(R.id.emptyCartLayout)
        startShoppingButton = view.findViewById(R.id.buttonStartShopping)
        placeOrderButton = view.findViewById(R.id.buttonPlaceOrder)

        setupRecyclerView()

        cartViewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            updateUI(cartItems)
        }

        startShoppingButton.setOnClickListener {
            if (isAdded) {
                findNavController().navigate(R.id.action_cartFragment_to_homeFragment)
            }
        }

        placeOrderButton.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_orderSummaryFragment)
        }

        return view
    }

    private fun setupRecyclerView() {
        recyclerViewCart.layoutManager = GridLayoutManager(requireContext(), 2)
        cartAdapter = CartAdapter(
            emptyList(),
            onRemoveClick = { cartViewModel.removeItem(it) },
            onQuantityChange = { item, newQuantity -> cartViewModel.updateQuantity(item, newQuantity) },
            onSelectionChanged = { showToolbar(it) }
        )
        recyclerViewCart.adapter = cartAdapter
    }

    private fun updateUI(cartItems: List<CartItem>) {
        if (cartItems.isEmpty()) {
            recyclerViewCart.visibility = View.GONE
            emptyCartLayout.visibility = View.VISIBLE
            placeOrderButton.visibility = View.GONE
        } else {
            recyclerViewCart.visibility = View.VISIBLE
            emptyCartLayout.visibility = View.GONE
            placeOrderButton.visibility = View.VISIBLE
            cartAdapter.updateItems(cartItems)
        }
    }

    private fun showToolbar(isVisible: Boolean) {
        if (isVisible) {
            if (actionMode == null) {
                actionMode = (activity as? AppCompatActivity)?.startSupportActionMode(actionModeCallback)
            }
            actionMode?.title = "${cartAdapter.getSelectedItems().size} selected"
        } else {
            actionMode?.finish()
        }
    }

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode?.menuInflater?.inflate(R.menu.cart_selection_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.menu_delete -> {
                    deleteSelectedItems()
                    mode?.finish()
                    return true
                }
            }
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            cartAdapter.clearSelection()
            actionMode = null
        }
    }

    private fun deleteSelectedItems() {
        val selectedItems = cartAdapter.getSelectedItems()
        if (selectedItems.isNotEmpty()) {
            cartViewModel.removeSelectedItems(selectedItems)
        }
        cartAdapter.clearSelection()
    }
}
