package com.upb.pokeapi.ui.directorio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upb.pokeapi.BuildConfig
import com.upb.pokeapi.data.DirectorioRepository
import com.upb.pokeapi.data.remote.cuerpoDeBusqueda
import com.upb.pokeapi.model.UsuarioDirectorio
import com.upb.pokeapi.util.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DirectorioUiState(
    val texto: String = "",
    val cargando: Boolean = false,
    val usuarios: List<UsuarioDirectorio> = emptyList(),
    val buscado: Boolean = false,
    val error: String? = null
)

class DirectorioViewModel(
    private val repositorio: DirectorioRepository = DirectorioRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DirectorioUiState())
    val uiState: StateFlow<DirectorioUiState> = _uiState.asStateFlow()

    fun onTextoCambia(valor: String) = _uiState.update { it.copy(texto = valor) }

    fun buscar() {
        val actual = _uiState.value
        if (actual.cargando) return
        if (BuildConfig.API_KEY.isBlank()) {
            _uiState.update {
                it.copy(error = "Falta API_KEY en local.properties (paso 13 del manual). Agrégala y vuelve a compilar.")
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            when (val resultado = repositorio.buscar(cuerpoDeBusqueda(actual.texto))) {
                is Resultado.Exito -> _uiState.update {
                    it.copy(cargando = false, usuarios = resultado.datos, buscado = true)
                }
                is Resultado.Error -> _uiState.update { it.copy(cargando = false, error = resultado.mensaje) }
            }
        }
    }
}
