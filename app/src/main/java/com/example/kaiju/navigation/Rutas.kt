package com.example.kaiju.navigation

sealed class Rutas(val ruta: String) {
    object Login : Rutas("login")
    object Catalogo : Rutas("catalogo")
    object FormularioProducto : Rutas("formularioProducto")
    object Movimiento : Rutas("movimiento")
    object HistorialMovimientos : Rutas("historialMovimientos")
    object Alertas : Rutas("alertas")
    object Ranking : Rutas("ranking")

    // El detalle recibe el código del producto dentro de la ruta.
    object DetalleProducto : Rutas("detalleProducto/{codigo}") {
        // Arma la ruta real, por ejemplo "detalleProducto/k1"
        fun crearRuta(codigo: String): String = "detalleProducto/$codigo"
    }
}