package com.upb.pokeapi.ui.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.upb.pokeapi.model.PokemonDetalle
import com.upb.pokeapi.ui.components.MensajeDeError
import com.upb.pokeapi.ui.components.PantallaConBarra

@Composable
fun DetallePokemonScreen(
    nombre: String,
    onAtras: () -> Unit,
    viewModel: DetallePokemonViewModel = viewModel(factory = DetallePokemonViewModel.Factory(nombre))
) {
    val estado by viewModel.uiState.collectAsState()
    val error = estado.error
    val pokemon = estado.pokemon

    PantallaConBarra(
        titulo = nombre.replaceFirstChar { it.uppercase() },
        onAtras = onAtras
    ) { modifier ->
        when {
            estado.cargando -> Box(modifier, contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            error != null -> Box(modifier, contentAlignment = Alignment.Center) {
                MensajeDeError(mensaje = error, onReintentar = viewModel::cargar)
            }
            pokemon != null -> ContenidoDetalle(pokemon, modifier)
            else -> Unit
        }
    }
}

@Composable
private fun ContenidoDetalle(pokemon: PokemonDetalle, modifier: Modifier) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = pokemon.imagenUrl,
            contentDescription = pokemon.nombre,
            modifier = Modifier.size(200.dp)
        )
        Text(
            text = "#${pokemon.id} ${pokemon.nombre.replaceFirstChar { it.uppercase() }}",
            style = MaterialTheme.typography.headlineSmall
        )
        Text("Altura: ${pokemon.alturaMetros} m  ·  Peso: ${pokemon.pesoKg} kg")
        Text("Tipos: ${pokemon.tipos.joinToString(", ")}")
        Spacer(Modifier.height(16.dp))
        pokemon.estadisticas.forEach { estadistica ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(estadistica.nombre, modifier = Modifier.width(130.dp))
                LinearProgressIndicator(
                    progress = { estadistica.valor / 255f },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Text(estadistica.valor.toString())
            }
        }
    }
}
