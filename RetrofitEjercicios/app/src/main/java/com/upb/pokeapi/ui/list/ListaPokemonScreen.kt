package com.upb.pokeapi.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.upb.pokeapi.model.PokemonResumen
import com.upb.pokeapi.ui.components.MensajeDeError
import com.upb.pokeapi.ui.components.PantallaConBarra

@Composable
fun ListaPokemonScreen(
    onPokemonClick: (String) -> Unit,
    onPublicar: () -> Unit,
    onDirectorio: () -> Unit,
    viewModel: ListaPokemonViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val error = estado.error

    PantallaConBarra(
        titulo = "Pokémon",
        acciones = {
            TextButton(onClick = onPublicar) { Text("Publicar") }
            TextButton(onClick = onDirectorio) { Text("Directorio") }
        }
    ) { modifier ->
        when {
            estado.cargando -> Box(modifier, contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            error != null -> Box(modifier, contentAlignment = Alignment.Center) {
                MensajeDeError(mensaje = error, onReintentar = viewModel::cargar)
            }
            else -> LazyColumn(modifier) {
                items(estado.pokemones, key = { it.id }) { pokemon ->
                    FilaPokemon(pokemon = pokemon, onClick = { onPokemonClick(pokemon.nombre) })
                }
                item { PieDeLista(estado = estado, onCargarMas = viewModel::cargarMas) }
            }
        }
    }
}

@Composable
private fun FilaPokemon(pokemon: PokemonResumen, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = pokemon.imagenUrl,
            contentDescription = pokemon.nombre,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = "#${pokemon.id} ${pokemon.nombre.replaceFirstChar { it.uppercase() }}",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

/** Ultimo elemento de la lista: boton "Cargar más", indicador de carga o fin de la lista. */
@Composable
private fun PieDeLista(estado: ListaUiState, onCargarMas: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when {
            estado.cargandoMas -> CircularProgressIndicator()
            estado.hayMas -> {
                val errorMas = estado.errorAlCargarMas
                if (errorMas != null) {
                    Text(errorMas, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                }
                Button(onClick = onCargarMas) {
                    Text(if (errorMas != null) "Reintentar" else "Cargar más")
                }
            }
            else -> Text("No hay más Pokémon")
        }
    }
}
