package com.example.kaiju.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kaiju.model.Rol
import com.example.kaiju.ui.screen.PantallaCatalogo
import com.example.kaiju.ui.screen.PantallaDetalleProducto
import com.example.kaiju.ui.screen.PantallaLogin
import com.example.kaiju.ui.screen.PantallaMovimiento
import com.example.kaiju.viewmodel.InventarioViewModel
import com.example.kaiju.viewmodel.SesionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    sesionViewModel: SesionViewModel = viewModel(),
    inventarioViewModel: InventarioViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    val usuarioActivo by sesionViewModel.usuarioActivo.collectAsState()

    // Solo se muestran las barras si NO es la pantalla de Login y hay sesión activa
    val mostrarBarras = rutaActual != Rutas.Login.ruta && usuarioActivo != null

    Scaffold(
        topBar = {
            if (mostrarBarras) {
                TopAppBar(
                    title = { Text("Kaiju - ${usuarioActivo?.nombre} (${usuarioActivo?.rol})") },
                    actions = {
                        IconButton(onClick = {
                            sesionViewModel.cerrarSesion()
                            navController.navigate(Rutas.Login.ruta) {
                                popUpTo(0) { inclusive = true } // Limpia la pila por completo
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (mostrarBarras) {
                NavigationBar {
                    // 1. Catálogo (Todos los roles)
                    NavigationBarItem(
                        selected = rutaActual == Rutas.Catalogo.ruta,
                        onClick = { navController.navigate(Rutas.Catalogo.ruta) },
                        icon = { Icon(Icons.Default.List, contentDescription = null) },
                        label = { Text("Catálogo") }
                    )

                    // 2. Movimiento (Todos los roles)
                    NavigationBarItem(
                        selected = rutaActual == Rutas.Movimiento.ruta,
                        onClick = { navController.navigate(Rutas.Movimiento.ruta) },
                        icon = { Icon(Icons.Default.CompareArrows, contentDescription = null) },
                        label = { Text("Movimiento") }
                    )

                    // 3. Alertas (Administrador y Encargado de Inventario)
                    if (usuarioActivo?.rol == Rol.ADMINISTRADOR || usuarioActivo?.rol == Rol.ENCARGADO_INVENTARIO) {
                        NavigationBarItem(
                            selected = rutaActual == Rutas.Alertas.ruta,
                            onClick = { navController.navigate(Rutas.Alertas.ruta) },
                            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
                            label = { Text("Alertas") }
                        )
                    }

                    // 4. Crear Producto (Solo Administrador)
                    if (usuarioActivo?.rol == Rol.ADMINISTRADOR) {
                        NavigationBarItem(
                            selected = rutaActual == Rutas.FormularioProducto.ruta,
                            onClick = { navController.navigate(Rutas.FormularioProducto.ruta) },
                            icon = { Icon(Icons.Default.Add, contentDescription = null) },
                            label = { Text("Producto") }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.Login.ruta, // NavHost inicializa el grafo aquí
            modifier = Modifier.padding(innerPadding)
        ) {
            // RUTA 1: LOGIN
            composable(Rutas.Login.ruta) {
                PantallaLogin(
                    sesionViewModel = sesionViewModel,
                    onLoginExitoso = {
                        navController.navigate(Rutas.Catalogo.ruta) {
                            popUpTo(Rutas.Login.ruta) { inclusive = true }
                        }
                    }
                )
            }

            // RUTA 2: CATÁLOGO
            composable(Rutas.Catalogo.ruta) {
                PantallaCatalogo(
                    viewModel = inventarioViewModel,
                    onProductoClick = { codigo ->
                        navController.navigate(Rutas.DetalleProducto.crearRuta(codigo))
                    }
                )
            }

            // RUTA 3: MOVIMIENTO
            composable(Rutas.Movimiento.ruta) {
                PantallaMovimiento(
                    viewModel = inventarioViewModel,
                    usuarioActual = usuarioActivo?.nombre ?: "Anónimo"
                )
            }

            // RUTA 4: ALERTAS
            composable(Rutas.Alertas.ruta) {
                PantallaCatalogo(
                    viewModel = inventarioViewModel,
                    onProductoClick = { codigo ->
                        navController.navigate(Rutas.DetalleProducto.crearRuta(codigo))
                    }
                )
            }

            // RUTA 5: CREAR PRODUCTO
            composable(Rutas.FormularioProducto.ruta) {
                // Formulario de Producto cuando se integre
            }

            // RUTA 6: DETALLE PRODUCTO
            composable(
                route = Rutas.DetalleProducto.ruta,
                arguments = listOf(navArgument("codigo") { type = NavType.StringType })
            ) { backStackEntry ->
                val codigo = backStackEntry.arguments?.getString("codigo") ?: ""
                PantallaDetalleProducto(
                    codigoProducto = codigo,
                    viewModel = inventarioViewModel,
                    onVolver = { navController.popBackStack() }
                )
            }
        }
    }
}