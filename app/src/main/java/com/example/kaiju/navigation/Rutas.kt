package com.example.kaiju.navigation

sealed class Rutas(val ruta: String) {
    object Catalogo : Rutas("catalogo")
    object Login : Rutas("login")
    object DetalleProducto : Rutas("detalleProducto")
    object FormularioProducto : Rutas("formularioProducto")
    object Movimiento : Rutas("movimiento")
    object Alertas : Rutas("alertas")
}