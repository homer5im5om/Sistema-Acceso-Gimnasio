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
import org.example.project.exportarAExcel // Importamos nuestra nueva función

class BitacoraScreen(val database: GymDatabase) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val historial = remember { database.gymDatabaseQueries.obtenerHistorialAccesos().executeAsList() }

        // Variable para mostrarle al usuario si se guardó bien el Excel
        var mensajeExportacion by remember { mutableStateOf("") }

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Historial de Accesos", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            if (historial.isEmpty()) {
                Text("No hay registros de acceso todavía.", style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(historial) { registro ->
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

            // Mensaje de éxito o error al guardar
            if (mensajeExportacion.isNotEmpty()) {
                Text(text = mensajeExportacion, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // BOTÓN DE EXPORTAR A EXCEL
            Button(
                onClick = {
                    // 1. Armamos los encabezados de las columnas
                    val encabezados = "ID Bitacora,Nombre del Socio,Fecha y Hora,Estado del Acceso\n"

                    // 2. Transformamos la lista de la base de datos en filas de texto separadas por comas
                    val filas = historial.joinToString("\n") { registro ->
                        val estado = if (registro.acceso_permitido == 1L) "Concedido" else "Denegado"
                        "${registro.id_bitacora},${registro.nombre},${registro.fecha_hora},$estado"
                    }

                    // PRO TIP: "\uFEFF" le dice a Excel que use formato UTF-8 para que lea bien los acentos
                    val csvCompleto = "\uFEFF" + encabezados + filas

                    // 3. Llamamos a nuestra función multiplataforma
                    mensajeExportacion = exportarAExcel(csvCompleto, "Reporte_Accesos_Gym")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)) // Color verde Excel
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