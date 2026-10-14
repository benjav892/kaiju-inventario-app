package com.example.kaiju.navigation

sealed class Rutas(val ruta: String) {
    object Login : Rutas("login")
    object Catalogo : Rutas("catalogo")
    object DetalleProducto : Rutas("detalle_producto/{codigo}") {
        fun crearRuta(codigo: String) = "detalle_producto/$codigo"
    }
    object FormularioProducto : Rutas("formulario_producto")
    object Movimiento : Rutas("movimiento")
    object Alertas : Rutas("alertas")
}