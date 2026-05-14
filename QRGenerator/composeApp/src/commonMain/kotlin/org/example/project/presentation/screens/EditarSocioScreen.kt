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
import org.example.project.data.Usuario
import org.example.project.presentation.viewmodels.EditarSocioViewModel

// La pantalla recibe la base de datos y el usuario que se va a editar
class EditarSocioScreen(val database: GymDatabase, val usuario: Usuario) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        // Instanciamos el ViewModel
        val viewModel = remember { EditarSocioViewModel(database) }
        val uiState by viewModel.uiState.collectAsState()

        // Inicializamos los campos con los datos actuales del usuario
        var nombre by remember { mutableStateOf(usuario.nombre) }
        var password by remember { mutableStateOf(usuario.password) }
        var idEstado by remember { mutableStateOf(usuario.id_estado) }

        // Efecto: Cuando se actualiza con éxito, regresamos a la lista
        LaunchedEffect(uiState.actualizacionExitosa) {
            if (uiState.actualizacionExitosa) {
                navigator.pop()
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Editar Socio", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Token QR: ${usuario.qr_token}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    viewModel.limpiarError()
                },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    viewModel.limpiarError()
                },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // SELECCIÓN DE ESTADO DE MEMBRESÍA
            Text("Estado de Membresía:", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(0.8f).padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Activo
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = idEstado == 1L,
                        onClick = { idEstado = 1L }
                    )
                    Text("Activo")
                }
                // Vencido
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = idEstado == 2L,
                        onClick = { idEstado = 2L }
                    )
                    Text("Vencido")
                }
                // Suspendido
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = idEstado == 3L,
                        onClick = { idEstado = 3L }
                    )
                    Text("Suspendido")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.mensajeError.isNotEmpty()) {
                Text(
                    text = uiState.mensajeError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    viewModel.actualizarUsuario(usuario.id_usuario, nombre, password, idEstado)
                },
                modifier = Modifier.fillMaxWidth(0.8f).height(50.dp)
            ) {
                Text("Guardar Cambios")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navigator.pop() }) {
                Text("Cancelar")
            }
        }
    }
}