package com.upb.pokeapi.ui.publicaciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.upb.pokeapi.ui.components.PantallaConBarra

@Composable
fun NuevaPublicacionScreen(
    onAtras: () -> Unit,
    viewModel: NuevaPublicacionViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val creada = estado.creada
    val error = estado.error

    PantallaConBarra(titulo = "Nueva publicación", onAtras = onAtras) { modifier ->
        Column(
            modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = estado.titulo,
                onValueChange = viewModel::onTituloCambia,
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = estado.cuerpo,
                onValueChange = viewModel::onCuerpoCambia,
                label = { Text("Contenido") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = estado.usuarioId,
                onValueChange = viewModel::onUsuarioIdCambia,
                label = { Text("Id de usuario") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = viewModel::enviar, enabled = !estado.enviando) {
                Text(if (estado.enviando) "Enviando..." else "Enviar (POST)")
            }
            if (creada != null) {
                Text(
                    text = "Publicación creada con id ${creada.id}",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
                Text("JSONPlaceholder no guarda nada: responde como si hubiera creado el recurso.")
            }
            if (error != null) {
                Text(error, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
