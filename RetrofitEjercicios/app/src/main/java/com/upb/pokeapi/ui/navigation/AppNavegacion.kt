package com.upb.pokeapi.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.upb.pokeapi.ui.detail.DetallePokemonScreen
import com.upb.pokeapi.ui.directorio.DirectorioScreen
import com.upb.pokeapi.ui.list.ListaPokemonScreen
import com.upb.pokeapi.ui.publicaciones.NuevaPublicacionScreen

private const val RUTA_LISTA = "lista"
private const val RUTA_DETALLE = "detalle/{nombre}"
private const val RUTA_PUBLICAR = "publicar"
private const val RUTA_DIRECTORIO = "directorio"

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = RUTA_LISTA) {
        composable(RUTA_LISTA) {
            ListaPokemonScreen(
                // Ejercicio 1: el nombre viaja como argumento de la ruta.
                onPokemonClick = { nombre -> navController.navigate("detalle/${Uri.encode(nombre)}") },
                onPublicar = { navController.navigate(RUTA_PUBLICAR) },
                onDirectorio = { navController.navigate(RUTA_DIRECTORIO) }
            )
        }
        composable(
            route = RUTA_DETALLE,
            arguments = listOf(navArgument("nombre") { type = NavType.StringType })
        ) { entrada ->
            val nombre = entrada.arguments?.getString("nombre").orEmpty()
            DetallePokemonScreen(nombre = nombre, onAtras = { navController.popBackStack() })
        }
        composable(RUTA_PUBLICAR) {
            NuevaPublicacionScreen(onAtras = { navController.popBackStack() })
        }
        composable(RUTA_DIRECTORIO) {
            DirectorioScreen(onAtras = { navController.popBackStack() })
        }
    }
}
