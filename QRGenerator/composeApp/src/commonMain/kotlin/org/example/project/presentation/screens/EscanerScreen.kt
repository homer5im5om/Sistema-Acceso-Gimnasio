package org.example.project.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
// NUEVO: Importamos BackHandler para el botón físico del celular
import androidx.activity.compose.BackHandler
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

        // NUEVO 1: Variable para encender/apagar la cámara
        var camaraActiva by remember { mutableStateOf(true) }

        // NUEVO 2: Atrapamos el botón físico de "Atrás" del celular
        BackHandler {
            camaraActiva = false // Apagamos la cámara primero
            navigator.pop()      // Luego salimos
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = resultadoQr, style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.size(300.dp)) {
                // NUEVO 3: Solo mostramos la cámara si camaraActiva es true
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
                                    resultadoQr = "Acceso Concedido: ${usuario.nombre}"
                                    println("Socio encontrado: ${usuario.nombre}")
                                } else {
                                    resultadoQr = "Usuario no registrado"
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

            // NUEVO 4: El botón también apaga la cámara antes de salir
            Button(onClick = {
                camaraActiva = false
                navigator.pop()
            }) {
                Text("Regresar al Menú")
            }
        }
    }
}