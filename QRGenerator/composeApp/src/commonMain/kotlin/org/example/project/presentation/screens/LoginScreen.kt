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
import org.example.project.presentation.viewmodels.LoginViewModel // Importamos nuestro ViewModel

class LoginScreen(val database: GymDatabase) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        // 1. Instanciamos nuestro ViewModel
        val viewModel = remember { LoginViewModel(database) }

        // 2. Observamos el UIState reactivo
        val uiState by viewModel.uiState.collectAsState()

        var usuario by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        // 3. Efecto Secundario: Si el ViewModel nos dice que ya hay usuario, navegamos
        LaunchedEffect(uiState.usuarioLogueado) {
            if (uiState.usuarioLogueado != null) {
                navigator.push(DashboardScreen(database, uiState.usuarioLogueado!!))
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Acceso al Gimnasio", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = usuario,
                onValueChange = {
                    usuario = it
                    viewModel.limpiarError() // Le avisamos al ViewModel
                },
                label = { Text("Usuario") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    viewModel.limpiarError() // Le avisamos al ViewModel
                },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Leemos el error directamente desde el estado del ViewModel
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
                    // LA MAGIA MVVM: Solo le pasamos la chamba al ViewModel
                    viewModel.verificarLogin(usuario, password)
                }
            ) {
                Text("Ingresar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navigator.push(RegistroScreen(database)) }) {
                Text("¿No tienes cuenta? Regístrate aquí")
            }
        }
    }
}