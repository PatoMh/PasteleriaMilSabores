package com.example.pasteleriamilsabores.data.model

import java.text.NumberFormat
import java.util.Locale

data class Product(
    val code: String,
    val category: String,
    val name: String,
    val price: Int,
    val description: String,
    val imageUrl: String // URL o referencia a imagen simulada
)

data class CartItem(
    val product: Product,
    var quantity: Int
)

// Función auxiliar para formato de precio CLP (Chilean Peso)
fun formatPrice(price: Int): String {
    val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL"))
    format.maximumFractionDigits = 0
    return format.format(price)
}
