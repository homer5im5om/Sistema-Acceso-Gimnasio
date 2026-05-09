package org.example.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.example.project.data.GymDatabase
import org.example.project.presentation.screens.LoginScreen

@Composable
fun App(database: GymDatabase) { // <-- Agregamos el parámetro
    MaterialTheme {
        // Le pasamos la base de datos a tu pantalla
        Navigator(LoginScreen(database)) { navigator ->
            SlideTransition(navigator)
        }
    }
}