package com.upb.pokeapi.ui.directorio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.upb.pokeapi.model.UsuarioDirectorio
import com.upb.pokeapi.ui.components.MensajeDeError
import com.upb.pokeapi.ui.components.PantallaConBarra

@Composable
fun DirectorioScreen(
    onAtras: () -> Unit,
    viewModel: DirectorioViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val error = estado.error

    PantallaConBarra(titulo = "Directorio (API del curso)", onAtras = onAtras) { modifier ->
        Column(modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = estado.texto,
                    onValueChange = viewModel::onTextoCambia,
                    label = { Text("Buscar") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = viewModel::buscar, enabled = !estado.cargando) { Text("Buscar") }
            }
            when {
                estado.cargando -> Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                error != null -> MensajeDeError(mensaje = error, onReintentar = viewModel::buscar)
                estado.buscado && estado.usuarios.isEmpty() -> Text("Sin resultados")
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(estado.usuarios, key = { it.id }) { usuario -> TarjetaUsuario(usuario) }
                }
            }
        }
    }
}

@Composable
private fun TarjetaUsuario(usuario: UsuarioDirectorio) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(usuario.nombre, style = MaterialTheme.typography.titleMedium)
            val detalle = listOfNotNull(usuario.cargo, usuario.area).joinToString(" · ")
            if (detalle.isNotEmpty()) Text(detalle)
            usuario.contactos.forEach { contacto ->
                val marca = if (contacto.principal) " (principal)" else ""
                Text("${contacto.tipo}: ${contacto.valor}$marca", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
