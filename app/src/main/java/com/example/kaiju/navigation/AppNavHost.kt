package com.example.kaiju.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kaiju.model.FormularioMovimientoEstado
import com.example.kaiju.ui.screen.PantallaAlertas
import com.example.kaiju.ui.screen.PantallaCatalogo
import com.example.kaiju.ui.screen.PantallaDetalleProducto
import com.example.kaiju.ui.screen.PantallaFormularioProducto
import com.example.kaiju.ui.screen.PantallaLogin
import com.example.kaiju.ui.screen.PantallaMovimiento
import com.example.kaiju.ui.screen.PantallaRanking
import com.example.kaiju.viewmodel.InventarioViewModel
import com.example.kaiju.viewmodel.InventarioViewModelFactory
import com.example.kaiju.viewmodel.SesionViewModel

@Composable
fun AppNavHost(
    sesionViewModel: SesionViewModel = viewModel()
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Instanciación del ViewModel mediante la fábrica con Context
    val inventarioViewModel: InventarioViewModel = viewModel(
        factory = InventarioViewModelFactory(context)
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    // Suscripción a los estados compartidos
    val usuarioActivo by sesionViewModel.usuarioActivo.collectAsState()
    val productos by inventarioViewModel.productos.collectAsState()
    val movimientos by inventarioViewModel.movimientos.collectAsState()

    Scaffold(
        bottomBar = {
            // El menú se despliega únicamente si el usuario inició sesión y está fuera de Login
            if (rutaActual != null && rutaActual != Rutas.Login.ruta && usuarioActivo != null) {
                NavigationBar {
                    // 1. Catálogo
                    NavigationBarItem(
                        selected = rutaActual == Rutas.Catalogo.ruta,
                        onClick = { navController.navigate(Rutas.Catalogo.ruta) { launchSingleTop = true } },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                        label = { Text("Catálogo") }
                    )

                    // 2. Movimiento
                    NavigationBarItem(
                        selected = rutaActual == Rutas.Movimiento.ruta,
                        onClick = { navController.navigate(Rutas.Movimiento.ruta) { launchSingleTop = true } },
                        icon = { Icon(Icons.Filled.Refresh, contentDescription = null) },
                        label = { Text("Movimiento") }
                    )

                    // 3. Alertas
                    if (sesionViewModel.puedeVerAlertas()) {
                        NavigationBarItem(
                            selected = rutaActual == Rutas.Alertas.ruta,
                            onClick = { navController.navigate(Rutas.Alertas.ruta) { launchSingleTop = true } },
                            icon = { Icon(Icons.Filled.Warning, contentDescription = null) },
                            label = { Text("Alertas") }
                        )
                    }

                    // 4. Ranking
                    NavigationBarItem(
                        selected = rutaActual == Rutas.Ranking.ruta,
                        onClick = { navController.navigate(Rutas.Ranking.ruta) { launchSingleTop = true } },
                        icon = { Icon(Icons.Filled.Star, contentDescription = null) },
                        label = { Text("Ranking") }
                    )

                    // 5. Agregar
                    if (sesionViewModel.puedeCrearEditarProductos()) {
                        NavigationBarItem(
                            selected = rutaActual == Rutas.FormularioProducto.ruta,
                            onClick = { navController.navigate(Rutas.FormularioProducto.ruta) { launchSingleTop = true } },
                            icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                            label = { Text("Agregar") }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.Login.ruta,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Login
            composable(Rutas.Login.ruta) {
                PantallaLogin(
                    sesionViewModel = sesionViewModel,
                    onLoginExitoso = {
                        navController.navigate(Rutas.Catalogo.ruta) {
                            popUpTo(Rutas.Login.ruta) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // 2. Catálogo
            composable(Rutas.Catalogo.ruta) {
                PantallaCatalogo(
                    viewModel = inventarioViewModel,
                    onProductoClick = { codigo ->
                        navController.navigate(Rutas.DetalleProducto.crearRuta(codigo))
                    }
                )
            }

            // 3. Formulario Agregar Producto
            composable(Rutas.FormularioProducto.ruta) {
                PantallaFormularioProducto(
                    viewModel = inventarioViewModel,
                    onProductoGuardado = { navController.popBackStack() }
                )
            }

            // 4. Detalle de Producto
            composable(
                route = Rutas.DetalleProducto.ruta,
                arguments = listOf(navArgument("codigo") { type = NavType.StringType })
            ) { entry ->
                val codigoProducto = entry.arguments?.getString("codigo") ?: ""
                val productoSeleccionado = productos.find { it.codigo == codigoProducto }

                PantallaDetalleProducto(
                    producto = productoSeleccionado,
                    movimientos = movimientos.filter { it.codigoProducto == codigoProducto },
                    puedeEditarProducto = sesionViewModel.puedeCrearEditarProductos(),
                    onVolver = { navController.popBackStack() },
                    onIrARegistrarMovimiento = { _ ->
                        navController.navigate(Rutas.Movimiento.ruta) { launchSingleTop = true }
                    },
                    onIrAEditarProducto = { _ ->
                        navController.navigate(Rutas.FormularioProducto.ruta)
                    }
                )
            }

            // 5. Alertas
            composable(Rutas.Alertas.ruta) {
                val productosEnAlerta = productos.filter { it.stockActual <= it.stockMinimo }
                PantallaAlertas(
                    productosEnAlerta = productosEnAlerta,
                    onProductoClick = { codigo ->
                        navController.navigate(Rutas.DetalleProducto.crearRuta(codigo))
                    }
                )
            }

            // 6. Movimientos
            composable(Rutas.Movimiento.ruta) {
                PantallaMovimiento(
                    estado = FormularioMovimientoEstado(),
                    productos = productos,
                    tiposPermitidos = sesionViewModel.obtenerTiposMovimientoPermitidos(),
                    onCodigoProductoChange = { },
                    onTipoChange = { },
                    onCantidadChange = { },
                    onMotivoChange = { },
                    onSubmit = { navController.popBackStack() },
                    onVolver = { navController.popBackStack() }
                )
            }

            // 7. Ranking
            composable(Rutas.Ranking.ruta) {
                PantallaRanking(
                    productos = productos,
                    movimientos = movimientos,
                    onVolver = { navController.popBackStack() },
                    onProductoClick = { codigo ->
                        navController.navigate(Rutas.DetalleProducto.crearRuta(codigo))
                    }
                )
            }
        }
    }
}