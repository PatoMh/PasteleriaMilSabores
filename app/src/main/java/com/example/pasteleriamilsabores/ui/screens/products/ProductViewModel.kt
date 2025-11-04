package com.example.pasteleriamilsabores.ui.screens.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pasteleriamilsabores.data.model.CartItem
import com.example.pasteleriamilsabores.data.model.Product
import com.example.pasteleriamilsabores.data.model.formatPrice
import com.example.pasteleriamilsabores.data.repository.ProductRepository

class ProductViewModel : ViewModel() {
    private val allProducts = ProductRepository.getProducts()

    // LiveData para el carrito de compras
    private val _cart = MutableLiveData<List<CartItem>>(emptyList())
    val cart: LiveData<List<CartItem>> = _cart

    // LiveData para la búsqueda (filtro)
    private val _searchQuery = MutableLiveData("")

    private val _filteredProducts = MutableLiveData(allProducts)
    // Exponemos los productos filtrados
    val products: LiveData<List<Product>> = _filteredProducts


    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        // Implementación de filtro simple en el ViewModel
        _filteredProducts.value = allProducts.filter {
            it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true)
        }
    }

    fun addToCart(product: Product) {
        val currentCart = _cart.value.orEmpty().toMutableList()
        val existingItem = currentCart.find { it.product.code == product.code }

        if (existingItem != null) {
            val updatedCart = currentCart.map {
                if (it.product.code == product.code) {
                    it.copy(quantity = it.quantity + 1)
                } else {
                    it
                }
            }
            _cart.value = updatedCart
        } else {
            currentCart.add(CartItem(product, 1))
            _cart.value = currentCart
        }
    }

    fun updateCartItemQuantity(productCode: String, quantity: Int) {
        val currentCart = _cart.value.orEmpty()
        val updatedCart = currentCart.map {
            if (it.product.code == productCode) {
                it.copy(quantity = quantity)
            } else {
                it
            }
        }.filter { it.quantity > 0 } // Eliminar si la cantidad es cero o menos
        _cart.value = updatedCart
    }

    fun removeCartItem(productCode: String) {
        _cart.value = _cart.value.orEmpty().filter { it.product.code != productCode }
    }

    fun sendSaleToBackend() {
        if (_cart.value.isNullOrEmpty()) {
            println("El carrito está vacío. Venta no enviada.")
            return
        }
        val total = _cart.value.orEmpty().sumOf { it.product.price * it.quantity }
        println("--- Venta Enviada al Backend ---")
        println("Productos en el Carrito: ${_cart.value?.size}")
        println("Total de la Venta (CLP): ${formatPrice(total)}")
        println("Simulación: Se registraría la venta en la Base de Datos.")

        // Limpiar carrito después de simular la venta
        _cart.value = emptyList()
    }
}
