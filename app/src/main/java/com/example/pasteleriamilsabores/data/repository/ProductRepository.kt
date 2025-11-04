package com.example.pasteleriamilsabores.data.repository

import com.example.pasteleriamilsabores.data.model.Product

object ProductRepository {
    fun getProducts(): List<Product> {
        return listOf(
            Product("TC001", "Tortas Cuadradas", "Torta Cuadrada de Chocolate", 45000, "Deliciosa torta de chocolate con ganache.", "https://i.pinimg.com/736x/9b/b5/5e/9bb55e1f12e1fc9cdba332cd860bcd92.jpg"),
            Product("TC002", "Tortas Cuadradas", "Torta Cuadrada de Frutas", 50000, "Mezcla de frutas frescas y crema chantilly.", "https://i.pinimg.com/originals/fd/e3/f9/fde3f95ac07c4902a4977b542c20a017.jpg"),
            Product("TT001", "Tortas Circulares", "Torta Circular de Vainilla", 40000, "Bizcocho clásico con crema pastelera.", "https://www.pasteleriaelparron.cl/wp-content/uploads/2023/08/8-Hoja-Crema-Vainilla.jpg"),
            Product("TT002", "Tortas Circulares", "Torta Circular de Manjar", 42000, "Torta tradicional chilena con manjar y nueces.", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTqw7uCfOiEWXEIDTFqRygVCu1dtJfLj5jheg&s"),
            Product("PI001", "Postres Individuales", "Mousse de Chocolate", 5000, "Postre individual cremoso y suave.", "https://cdn.recetasderechupete.com/wp-content/uploads/2021/05/Tarta-mousse-de-chocolate.jpg"),
            Product("PI002", "Postres Individuales", "Tiramisú Clásico", 5500, "El clásico postre italiano con un toque especial.", "https://recetas123.net/wp-content/uploads/tiramisu.jpg"),
            Product("PSA001", "Productos Sin Azúcar", "Torta Sin Azúcar de Naranja", 48000, "Torta ligera y deliciosa, endulzada naturalmente.", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSEg7DdGVY4YZcwiCWFjtu4ulXpYv5pfRuJhQ&s"),
            Product("PSA002", "Productos Sin Azúcar", "Cheesecake Sin Azúcar", 47000, "Cheesecake cremoso y sin azúcar, ideal para todos.", "https://www.homecookingadventure.com/wp-content/uploads/2022/02/low_fat_refined_sugar_free_cheesecake_main-2.jpg"),
            Product("PT001", "Pastelería Tradicional", "Empanada de Manzana", 3000, "Empanada horneada con trozos de manzana y canela.", "https://cdn0.uncomo.com/es/posts/8/1/9/como_hacer_empanadillas_de_manzana_53918_600.jpg"),
            Product("PT002", "Pastelería Tradicional", "Tarta de Santiago", 6000, "Tarta de almendras, un clásico de la repostería española.", "https://cdn0.recetasgratis.net/es/posts/8/5/8/tarta_de_santiago_tradicional_51858_orig.jpg"),
            Product("PG001", "Productos Sin Gluten", "Brownie Sin Gluten", 4000, "Brownie de chocolate denso y húmedo, libre de gluten.", "https://www.hola.com/horizon/landscape/14aee576ab21-brownie-sin-gluten-t.jpg"),
            Product("PG002", "Productos Sin Gluten", "Pan Sin Gluten", 3500, "Pan de molde suave y esponjoso, sin gluten.", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSCtDKtfLwuH0Tu9S-05BSKytxFGzKiYqQYKA&s"),
            Product("PV001", "Productos Vegana", "Torta Vegana de Chocolate", 50000, "Torta de chocolate 100% vegana, con ingredientes de origen vegetal.", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRvfVV7HRqQMyLsZ4qglpOVwO17pkW7jAAlIg&s"),
            Product("PV002", "Productos Vegana", "Galletas Veganas de Avena", 4500, "Galletas de avena y pasas, endulzadas con sirope de agave.", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRjVmQ-vBWXZeh3ahpaFPZX7MA3gnZbeys4qQ&s"),
            Product("TE001", "Tortas Especiales", "Torta Especial de Cumpleaños", 55000, "Torta personalizada para celebraciones de cumpleaños.", "https://i.pinimg.com/474x/25/60/c7/2560c73a21554191f7b1668c041563ce.jpg"),
            Product("TE002", "Tortas Especiales", "Torta Especial de Boda", 60000, "Torta elegante de varios pisos para bodas y eventos.", "https://cdn0.matrimonios.cl/article-vendor/6959/original/1280/jpg/file-1652369776963_8_116959-165236977830162.jpeg")
        )
    }
}
