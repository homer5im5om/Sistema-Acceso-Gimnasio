package org.example.project.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import org.example.project.data.GymDatabase
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

// 1. Tu clase ahora pide la base de datos para funcionar
class LoginScreen(val database: GymDatabase) : Screen {

    @Composable
    override fun Content() {
        var usuario by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        val navigator = LocalNavigator.currentOrThrow

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Acceso al Gimnasio", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = usuario,
                onValueChange = { usuario = it },
                label = { Text("Usuario") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    // AQUÍ ESTÁ LA MAGIA: Le preguntamos a SQL si existen estos datos
                    val usuarioEncontrado = database.gymDatabaseQueries
                        .verificarLogin(usuario, password)
                        .executeAsOneOrNull() // executeAsOneOrNull trae 1 resultado exacto o se queda nulo

                    if (usuarioEncontrado != null) {
                        println("¡BINGO! Bienvenido, ${usuarioEncontrado.nombre}")
                        // Esta es la magia de Voyager para cambiar de pantalla:
                        navigator.push(DashboardScreen(database))
                    } else {
                        println("ERROR: Credenciales incorrectas")
                    }
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