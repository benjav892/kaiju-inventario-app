package com.example.kaiju.repository

import com.example.kaiju.model.*

object DatosDePrueba {
    val usuarios = listOf(
        Usuario("U01", "Benjamín Vasquez", Rol.ADMINISTRADOR),
        Usuario("U02", "Sebastián Toro", Rol.VENDEDOR),
        Usuario("U03", "Camilo Vera", Rol.ENCARGADO_INVENTARIO)
    )

    val productos = listOf(
        // Productos con Stock Normal
        Producto("PROD01", "Polera Kaiju Classic", "Polera 100% algodón", "Ropa", "Polera", 15990, 15, 5),
        Producto("PROD02", "Figura Godzilla 15cm", "Coleccionable articulado", "Coleccionables", "Figura", 24990, 8, 3),
        Producto("PROD03", "Mochila King Ghidorah", "Mochila escolar reforzada", "Accesorios", "Mochila", 32990, 12, 4),
        Producto("PROD04", "Polerón Mechagodzilla", "Polerón con capucha", "Ropa", "Polerón", 28990, 10, 3),
        Producto("PROD05", "Comic Kaiju Vol. 1", "Edición especial tapa dura", "Libros", "Comic", 12990, 20, 5),
        Producto("PROD06", "Llavero Rodan Metal", "Llavero metálico antioxidante", "Accesorios", "Llavero", 4990, 30, 10),
        Producto("PROD07", "Gorra Kaiju Neon", "Gorra bordada plana", "Ropa", "Gorra", 11990, 14, 4),

        // Productos en STOCK BAJO (stockActual <= stockMinimo)
        Producto("PROD08", "Taza Mothra Cerámica", "Taza de 350ml", "Accesorios", "Taza", 8990, 2, 5),
        Producto("PROD09", "Posters Set Kaiju", "Set de 4 afiches", "Decoración", "Poster", 9990, 1, 4),
        Producto("PROD10", "Peluche Chibi Rodan", "Peluche suave 20cm", "Juguetes", "Peluche", 14990, 0, 3)
    )
}