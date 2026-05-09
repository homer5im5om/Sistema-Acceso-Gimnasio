package org.example.project.presentation.screens

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
import qrscanner.QrScanner

class EscanerScreen(val database: GymDatabase) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        var resultadoQr by remember { mutableStateOf("Apunta a un código QR...") }

        // 1. Agrega esta variable hasta arriba de tu función Content(),
// justo debajo de donde declaraste resultadoQr
        var ultimoCodigoLeido by remember { mutableStateOf("") }

        // 1. Agregamos estas variables que la librería nos exige
        var linternaPrendida by remember { mutableStateOf(false) }
        var abrirGaleria by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = resultadoQr, style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier.size(300.dp)
            ) {
                QrScanner(
                    modifier = Modifier.fillMaxSize(),
                    // 2. Le pasamos los valores obligatorios aquí:
                    flashlightOn = linternaPrendida,
                    openImagePicker = abrirGaleria,
                    onCompletion = { codigoLeido ->
                        // Solo buscamos en la base de datos si es un código NUEVO
                        if (codigoLeido != ultimoCodigoLeido) {
                            ultimoCodigoLeido = codigoLeido // Guardamos el código para no repetirlo

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
                    imagePickerHandler = { estado ->
                        abrirGaleria = estado
                    },
                    onFailure = { error ->
                        println("Problema con la cámara: $error")
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { navigator.pop() }) {
                Text("Regresar al Menú")
            }
        }
    }
}