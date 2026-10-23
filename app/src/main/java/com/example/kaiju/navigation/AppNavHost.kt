package com.example.kaiju.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CompareArrows
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kaiju.database.KaijuDatabase
import com.example.kaiju.repository.InventarioRepository
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
fun AppNavHost() {
    val context = LocalContext.current

    // Instanciación de Room y Repositorio para inyectar al InventarioViewModel
    val database = KaijuDatabase.obtenerInstancia(context)
    val repository = remember { InventarioRepository(database.productoDao(), database.movimientoDao()) }

    val viewModel: InventarioViewModel = viewModel(
        factory = InventarioViewModelFactory(repository)
    )
    val sesionViewModel: SesionViewModel = viewModel()

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    // Suscripción reactiva a los datos de Room
    val productos by viewModel.productos.collectAsState()
    val movimientos by viewModel.movimientos.collectAsState()
    val usuarioActivo by sesionViewModel.usuarioActivo.collectAsState()

    Scaffold(
        bottomBar = {
            // El menú inferior solo se muestra si el usuario ya inició sesión
            if (usuarioActivo != null) {
                NavigationBar {
                    NavigationBarItem(
                        selected = rutaActual == Rutas.Catalogo.ruta,
                        onClick = { navController.navigate(Rutas.Catalogo.ruta) { launchSingleTop = true } },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                        label = { Text("Catálogo") }
                    )
                    NavigationBarItem(
                        selected = rutaActual == Rutas.Alertas.ruta,
                        onClick = { navController.navigate(Rutas.Alertas.ruta) { launchSingleTop = true } },
                        icon = { Icon(Icons.Filled.Warning, contentDescription = null) },
                        label = { Text("Alertas") }
                    )
                    NavigationBarItem(
                        selected = rutaActual == Rutas.Movimiento.ruta,
                        onClick = { navController.navigate(Rutas.Movimiento.ruta) { launchSingleTop = true } },
                        icon = { Icon(Icons.Filled.CompareArrows, contentDescription = null) },
                        label = { Text("Movimiento") }
                    )
                    NavigationBarItem(
                        selected = rutaActual == Rutas.Ranking.ruta,
                        onClick = { navController.navigate(Rutas.Ranking.ruta) { launchSingleTop = true } },
                        icon = { Icon(Icons.Filled.Star, contentDescription = null) },
                        label = { Text("Ranking") }
                    )
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
                        }
                    }
                )
            }

            // 2. Catálogo
            composable(Rutas.Catalogo.ruta) {
                PantallaCatalogo(
                    viewModel = viewModel,
                    onProductoClick = { codigo ->
                        navController.navigate(Rutas.DetalleProducto.crearRuta(codigo))
                    }
                )
            }

            // 3. Formulario Agregar/Editar Producto (Solo Admin)
            composable(Rutas.FormularioProducto.ruta) {
                PantallaFormularioProducto(
                    viewModel = viewModel,
                    onProductoGuardado = { navController.popBackStack() }
                )
            }

            // 4. Detalle de Producto (Integra tu lista de movimientos desde Room)
            composable(
                route = Rutas.DetalleProducto.ruta,
                arguments = listOf(navArgument("codigo") { type = NavType.StringType })
            ) { entry ->
                val codigo = entry.arguments?.getString("codigo") ?: ""

                PantallaDetalleProducto(
                    producto = productos.find { it.codigo == codigo },
                    movimientos = movimientos, // Se pasan los movimientos reales obtenidos de Room
                    puedeEditarProducto = sesionViewModel.puedeCrearEditarProductos(),
                    onVolver = { navController.popBackStack() },
                    onIrARegistrarMovimiento = { _ -> navController.navigate(Rutas.Movimiento.ruta) },
                    onIrAEditarProducto = { _ -> navController.navigate(Rutas.FormularioProducto.ruta) }
                )
            }

            // 5. Alertas de Stock Bajo (RF-09 / RN-06)
            composable(Rutas.Alertas.ruta) {
                val productosEnAlerta = productos.filter { it.stockActual <= it.stockMinimo }
                PantallaAlertas(
                    productosEnAlerta = productosEnAlerta,
                    onProductoClick = { codigo ->
                        navController.navigate(Rutas.DetalleProducto.crearRuta(codigo))
                    }
                )
            }

            // 6. Registrar Movimiento de Stock (RF-06/07/08)
            composable(Rutas.Movimiento.ruta) {
                PantallaMovimiento(
                    estado = com.example.kaiju.model.FormularioMovimientoEstado(),
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

            // 7. Módulo Propio: Ranking de Ventas y Tendencias por Categoría
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