package com.upb.pokeapi.ui.publicaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upb.pokeapi.data.PublicacionesRepository
import com.upb.pokeapi.model.Publicacion
import com.upb.pokeapi.util.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormularioUiState(
    val titulo: String = "",
    val cuerpo: String = "",
    val usuarioId: String = "1",
    val enviando: Boolean = false,
    val creada: Publicacion? = null,
    val error: String? = null
)

class NuevaPublicacionViewModel(
    private val repositorio: PublicacionesRepository = PublicacionesRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FormularioUiState())
    val uiState: StateFlow<FormularioUiState> = _uiState.asStateFlow()

    fun onTituloCambia(valor: String) = _uiState.update { it.copy(titulo = valor) }
    fun onCuerpoCambia(valor: String) = _uiState.update { it.copy(cuerpo = valor) }
    fun onUsuarioIdCambia(valor: String) = _uiState.update { it.copy(usuarioId = valor) }

    fun enviar() {
        val actual = _uiState.value
        if (actual.enviando) return
        val usuarioId = actual.usuarioId.toIntOrNull()
        when {
            actual.titulo.isBlank() || actual.cuerpo.isBlank() -> {
                _uiState.update { it.copy(error = "Completa el título y el contenido.", creada = null) }
                return
            }
            usuarioId == null -> {
                _uiState.update { it.copy(error = "El id de usuario debe ser un número.", creada = null) }
                return
            }
        }
        viewModelScope.launch {
            _uiState.update { it.copy(enviando = true, error = null, creada = null) }
            when (val resultado = repositorio.crear(actual.titulo.trim(), actual.cuerpo.trim(), usuarioId!!)) {
                is Resultado.Exito -> _uiState.update { it.copy(enviando = false, creada = resultado.datos) }
                is Resultado.Error -> _uiState.update { it.copy(enviando = false, error = resultado.mensaje) }
            }
        }
    }
}
