package com.upb.pokeapi.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upb.pokeapi.data.PokemonRepository
import com.upb.pokeapi.model.PokemonResumen
import com.upb.pokeapi.util.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ListaUiState(
    val cargando: Boolean = false,
    val cargandoMas: Boolean = false,
    val pokemones: List<PokemonResumen> = emptyList(),
    val error: String? = null,
    val errorAlCargarMas: String? = null,
    val hayMas: Boolean = true
)

class ListaPokemonViewModel(
    private val repositorio: PokemonRepository = PokemonRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListaUiState())
    val uiState: StateFlow<ListaUiState> = _uiState.asStateFlow()

    init {
        cargar()
    }

    /** Primera pagina (offset 0). Sirve tambien como Reintentar. */
    fun cargar() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            when (val resultado = repositorio.obtenerPokemones(TAMANO_PAGINA, 0)) {
                is Resultado.Exito -> _uiState.update {
                    it.copy(
                        cargando = false,
                        pokemones = resultado.datos,
                        hayMas = resultado.datos.size == TAMANO_PAGINA
                    )
                }
                is Resultado.Error -> _uiState.update { it.copy(cargando = false, error = resultado.mensaje) }
            }
        }
    }

    /** Pagina siguiente: el offset es la cantidad de pokemones que ya tenemos. */
    fun cargarMas() {
        val actual = _uiState.value
        if (actual.cargando || actual.cargandoMas || !actual.hayMas) return
        viewModelScope.launch {
            _uiState.update { it.copy(cargandoMas = true, errorAlCargarMas = null) }
            when (val resultado = repositorio.obtenerPokemones(TAMANO_PAGINA, actual.pokemones.size)) {
                is Resultado.Exito -> _uiState.update {
                    it.copy(
                        cargandoMas = false,
                        pokemones = it.pokemones + resultado.datos,
                        hayMas = resultado.datos.size == TAMANO_PAGINA
                    )
                }
                is Resultado.Error -> _uiState.update {
                    it.copy(cargandoMas = false, errorAlCargarMas = resultado.mensaje)
                }
            }
        }
    }

    companion object {
        const val TAMANO_PAGINA = 20
    }
}
