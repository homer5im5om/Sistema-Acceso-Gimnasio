package org.example.project.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.data.GymDatabase
import org.example.project.presentation.viewmodels.BitacoraViewModel // Importamos el ViewModel

class BitacoraScreen(val database: GymDatabase) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        // 1. Instanciamos el ViewModel
        val viewModel = remember { BitacoraViewModel(database) }

        // 2. Observamos el estado reactivo
        val uiState by viewModel.uiState.collectAsState()

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Historial de Accesos", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.historial.isEmpty()) {
                Text("No hay registros de acceso todavía.", style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    // Usamos la lista que viene del UIState
                    items(uiState.historial) { registro ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Socio: ${registro.nombre}", style = MaterialTheme.typography.titleMedium)
                                Text(text = "Fecha: ${registro.fecha_hora}", style = MaterialTheme.typography.bodyMedium)

                                val colorEstado = if (registro.acceso_permitido == 1L) Color(0xFF4CAF50) else Color(0xFFF44336)
                                val textoEstado = if (registro.acceso_permitido == 1L) "✅ Acceso Concedido" else "❌ Acceso Denegado"

                                Text(text = textoEstado, color = colorEstado, style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostramos el mensaje (éxito/error) directamente desde el UIState
            if (uiState.mensajeExportacion.isNotEmpty()) {
                Text(text = uiState.mensajeExportacion, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // BOTÓN DE EXPORTAR A EXCEL (Ahora es "tonto", solo le avisa al ViewModel)
            Button(
                onClick = { viewModel.exportarHistorial() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text("Exportar a Excel (.csv)")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { navigator.pop() }, modifier = Modifier.fillMaxWidth()) {
                Text("Regresar al Menú")
            }
        }
    }
}