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
import org.example.project.data.Usuario // Make sure to import this!
import androidx.compose.foundation.Image
import qrgenerator.qrkitpainter.rememberQrKitPainter
// Pantalla principal post-login. Recibe el objeto completo del usuario autenticado
class DashboardScreen(val database: GymDatabase, val usuarioLogueado: Usuario) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Personalized welcome message
            Text("¡Bienvenido, ${usuarioLogueado.nombre}!", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(32.dp))

            // ====================================================
            // ROLE CONTROL LOGIC
            // ====================================================
            if (usuarioLogueado.id_rol == 1L) {
                // ADMIN VIEW (Rol 1)
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
            // VIEW DEL SOCIO (Rol 2)
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

                    // AQUÍ ESTÁ LA MAGIA DE QRKIT.
                    // Transformamos el string en una imagen QR real.
                    val qrPainter = rememberQrKitPainter(data = usuarioLogueado.qr_token)

                    Image(
                        painter = qrPainter,
                        contentDescription = "Código QR del Socio",
                        modifier = Modifier.size(220.dp) // Tamaño lo suficientemente grande para que lo lea la cámara
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Muestra el token en texto por si el admin necesita ingresarlo manualmente.
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

            // Botón de cierre de sesión disponible para ambos roles.
            TextButton(onClick = { navigator.popAll() }) {
                Text("Cerrar Sesión")
            }
        }
    }
}