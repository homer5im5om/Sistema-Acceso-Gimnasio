package org.example.project.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
// Eliminamos el import de BackHandler que causaba el conflicto
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.data.GymDatabase
import qrscanner.QrScanner

class EscanerScreen(val database: GymDatabase) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        var resultadoQr by remember { mutableStateOf("Apunta a un código QR...") }
        var ultimoCodigoLeido by remember { mutableStateOf("") }
        var linternaPrendida by remember { mutableStateOf(false) }
        var abrirGaleria by remember { mutableStateOf(false) }

        var camaraActiva by remember { mutableStateOf(true) }

        // ESTA ES LA SOLUCIÓN MULTIPLATAFORMA:
        // Le dice a la app que apague la cámara justo al destruir la pantalla
        DisposableEffect(Unit) {
            onDispose {
                camaraActiva = false
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = resultadoQr, style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.size(300.dp)) {
                if (camaraActiva) {
                    QrScanner(
                        modifier = Modifier.fillMaxSize(),
                        flashlightOn = linternaPrendida,
                        openImagePicker = abrirGaleria,
                        onCompletion = { codigoLeido ->
                            if (codigoLeido != ultimoCodigoLeido) {
                                ultimoCodigoLeido = codigoLeido

                                val usuario = database.gymDatabaseQueries.buscarPorToken(codigoLeido).executeAsOneOrNull()

                                if (usuario != null) {
                                    resultadoQr = "✅ Acceso Concedido: ${usuario.nombre}"
                                    println("Socio encontrado: ${usuario.nombre}")
                                } else {
                                    resultadoQr = "❌ Usuario no registrado"
                                    println("Token rechazado: $codigoLeido")
                                }
                            }
                        },
                        imagePickerHandler = { estado -> abrirGaleria = estado },
                        onFailure = { error -> println("Problema con la cámara: $error") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = {
                camaraActiva = false
                navigator.pop()
            }) {
                Text("Regresar al Menú")
            }
        }
    }
}