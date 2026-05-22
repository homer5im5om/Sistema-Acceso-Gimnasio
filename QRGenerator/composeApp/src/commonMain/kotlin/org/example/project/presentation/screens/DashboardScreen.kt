package org.example.project.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.data.GymDatabase
import org.example.project.data.Usuario
import qrgenerator.qrkitpainter.rememberQrKitPainter

class DashboardScreen(val database: GymDatabase, val usuarioLogueado: Usuario) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        // ESTADO NUEVO: Controla si la alerta de cerrar sesión está visible o no
        var mostrarDialogoSalir by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("¡Bienvenido, ${usuarioLogueado.nombre}!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))

            // ====================================================
            // LÓGICA DE ROLES
            // ====================================================
            if (usuarioLogueado.id_rol == 1L) {
                // VISTA ADMIN (Rol 1)
                Text("Panel de Administrador", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navigator.push(EscanerScreen(database)) },
                    modifier = Modifier.fillMaxWidth(0.7f).height(60.dp)
                ) {
                    Text("Escanear Código QR")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navigator.push(ListaSociosScreen(database)) }) {
                    Text("Ver Lista de Socios")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navigator.push(BitacoraScreen(database)) }) {
                    Text("Ver Bitácora de Accesos")
                }
            } else {
                // VISTA DEL SOCIO (Rol 2)
                Text("Panel de Socio", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Tu Pase de Acceso", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))

                        val qrPainter = rememberQrKitPainter(data = usuarioLogueado.qr_token)

                        Image(
                            painter = qrPainter,
                            contentDescription = "Código QR del Socio",
                            modifier = Modifier.size(220.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = usuarioLogueado.qr_token,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Muestra este código en la entrada del gimnasio.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // BOTÓN MODIFICADO: Ahora abre el diálogo en lugar de salir de golpe
            TextButton(onClick = { mostrarDialogoSalir = true }) {
                Text("Cerrar Sesión")
            }
        }

        // ====================================================
        // NUEVO: DIÁLOGO DE CONFIRMACIÓN
        // ====================================================
        if (mostrarDialogoSalir) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoSalir = false },
                title = { Text("Cerrar Sesión") },
                text = { Text("¿Estás seguro de que deseas salir de tu cuenta?") },
                confirmButton = {
                    Button(
                        onClick = {
                            mostrarDialogoSalir = false
                            navigator.popAll() // Aquí se hace el cierre real
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Sí, salir")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoSalir = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}