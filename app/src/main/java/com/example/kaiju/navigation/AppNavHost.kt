package com.example.kaiju.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kaiju.ui.screen.PantallaCatalogo
import com.example.kaiju.ui.screen.PantallaFormularioProducto
import com.example.kaiju.viewmodel.InventarioViewModel


@Composable
fun AppNavHost(viewModel: InventarioViewModel = viewModel()){
    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = rutaActual == Rutas.Catalogo.ruta,
                    onClick = { navController.navigate(Rutas.Catalogo.ruta){launchSingleTop = true} },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                    label = { Text("Catalogo") }
                )
                NavigationBarItem(
                    selected = rutaActual == Rutas.FormularioProducto.ruta,
                    onClick = { navController.navigate(Rutas.FormularioProducto.ruta){launchSingleTop = true} },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                    label = { Text("Inventario") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController= navController,
            startDestination = Rutas.Catalogo.ruta,
            modifier = Modifier.padding(innerPadding)
        ){
            composable(Rutas.Catalogo.ruta){
                PantallaCatalogo()
            }
            composable(Rutas.FormularioProducto.ruta){
                PantallaFormularioProducto(viewModel = viewModel)
            }
        }
    }

}