package org.example.project.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.data.GymDatabase

class DashboardScreen(val database: GymDatabase) : Screen {
    @Composable
    override fun Content() {
        // 1. Traemos el navegador de Voyager
        val navigator = LocalNavigator.currentOrThrow

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("¡Bienvenido al Menú Principal!", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(64.dp))

            // 2. Un botón grandote y vistoso para el escáner
            Button(
                onClick = {
                    navigator.push(EscanerScreen(database))
                },
                modifier = Modifier.fillMaxWidth(0.7f).height(60.dp)
            ) {
                Text("Escanear Código QR")
            }
        }
    }
}