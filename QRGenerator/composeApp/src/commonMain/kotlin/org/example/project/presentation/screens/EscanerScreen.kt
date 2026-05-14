package org.example.project.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.data.GymDatabase
import org.example.project.presentation.viewmodels.EscanerViewModel
import qrscanner.QrScanner

class EscanerScreen(val database: GymDatabase) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = remember { EscanerViewModel(database) }
        val uiState by viewModel.uiState.collectAsState()

        // VARIABLES NUEVAS: Requeridas por la librería QRKit para funcionar
        var flashlightOn by remember { mutableStateOf(false) }
        var openImagePicker by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Control de Acceso", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // Si NO hay resultado, abrimos la CÁMARA
            if (!uiState.mostrarResultado) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .aspectRatio(1f) // Lo hace cuadradito
                ) {
                    // COMPONENTE DE CÁMARA ACTUALIZADO
                    QrScanner(
                        modifier = Modifier.fillMaxSize(),
                        flashlightOn = flashlightOn, // Le pasamos el estado de la linterna
                        openImagePicker = openImagePicker, // Le pasamos el estado de la galería
                        onCompletion = { result ->
                            viewModel.procesarCodigoQR(result)
                        },
                        imagePickerHandler = { isOpen ->
                            openImagePicker = isOpen
                        },
                        onFailure = { error ->
                            println("Error al escanear: $error")
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para prender/apagar la linterna del celular
                Button(onClick = { flashlightOn = !flashlightOn }) {
                    Text(if (flashlightOn) "🔦 Apagar Linterna" else "🔦 Encender Linterna")
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Apunta la cámara al Código QR", style = MaterialTheme.typography.bodyMedium)

            }
            // Si SÍ hay resultado, mostramos la tarjeta VERDE o ROJA
            else {
                Card(
                    modifier = Modifier.fillMaxWidth(0.9f).padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.accesoPermitido) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.mensajeResultado,
                            style = MaterialTheme.typography.titleLarge,
                            color = if (uiState.accesoPermitido) Color(0xFF2E7D32) else Color(0xFFC62828),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                viewModel.limpiarResultado()
                                flashlightOn = false // Apagamos la linterna al continuar
                            },
                            modifier = Modifier.height(50.dp)
                        ) {
                            Text("Escanear Siguiente")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            TextButton(onClick = { navigator.pop() }) {
                Text("Regresar al Menú")
            }
        }
    }
}