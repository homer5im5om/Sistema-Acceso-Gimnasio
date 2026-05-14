package org.example.project.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.data.GymDatabase
import org.example.project.presentation.viewmodels.RegistroViewModel

class RegistroScreen(val database: GymDatabase) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        // Initialize the ViewModel and observe its state
        val viewModel = remember { RegistroViewModel(database) }
        val uiState by viewModel.uiState.collectAsState()

        var nombre by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        // Automatically navigate back to Login when registration succeeds
        LaunchedEffect(uiState.registroExitoso) {
            if (uiState.registroExitoso) {
                navigator.pop()
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Registro de Nuevo Socio", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    viewModel.limpiarError()
                },
                label = { Text("Nombre Completo") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    viewModel.limpiarError()
                },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Display error messages from the ViewModel if any exist
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
                    viewModel.registrarUsuario(nombre, password)
                },
                modifier = Modifier.height(50.dp)
            ) {
                Text("Crear Cuenta")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navigator.pop() }) {
                Text("Cancelar y Regresar")
            }
        }
    }
}