package com.example.kaiju.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kaiju.model.PokemonDto
import com.example.kaiju.ui.components.BarraSuperior
import com.example.kaiju.viewmodel.PokedexViewModel
import androidx.compose.foundation.layout.size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage

@Composable
fun PantallaPokedex(
    viewModel: PokedexViewModel,
    onVolver: () -> Unit
) {
    val estado by viewModel.uiState.collectAsState()
    // Copias locales: así Kotlin permite usarlas sin "!!" después de comprobar null.
    val error = estado.error
    val pokemon = estado.pokemon

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Pokédex",
                mostrarBotonAtras = true,
                onNavegarAtras = onVolver
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Busca por nombre o número. Por ejemplo: pikachu o 25.")

            OutlinedTextField(
                value = estado.consulta,
                onValueChange = viewModel::actualizarConsulta,
                label = { Text("Nombre o número") },
                singleLine = true,
                enabled = !estado.cargando,
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = viewModel::buscar, enabled = !estado.cargando) {
                Text("Buscar")
            }

            when {
                estado.cargando -> {
                    CircularProgressIndicator()
                    Text("Consultando PokéAPI…")
                }
                error != null -> {
                    Text(error, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = viewModel::buscar) { Text("Reintentar") }
                }
                pokemon != null -> FichaPokemon(pokemon)
                else -> Text("Escribe un Pokémon y presiona Buscar.")
            }
        }
    }
}

@Composable
private fun FichaPokemon(pokemon: PokemonDto) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "#${pokemon.id} · ${pokemon.name}",
                style = MaterialTheme.typography.headlineSmall
            )
            if (pokemon.sprites.frontDefault == null) {
                Text("Imagen no disponible")
            } else {
                AsyncImage(
                    model = pokemon.sprites.frontDefault,
                    contentDescription = "Imagen de ${pokemon.name}",
                    error = painterResource(android.R.drawable.ic_menu_report_image),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(160.dp)
                )
            }
            Text(

                "Tipos: " + pokemon.types
                    .sortedBy { it.slot }
                    .joinToString { it.type.name }
            )
            Text("Altura: ${pokemon.height / 10.0} m")
            Text("Peso: ${pokemon.weight / 10.0} kg")
        }
    }
}