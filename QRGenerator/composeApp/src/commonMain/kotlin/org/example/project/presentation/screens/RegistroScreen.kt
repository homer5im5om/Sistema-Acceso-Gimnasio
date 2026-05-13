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

class RegistroScreen(val database: GymDatabase) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        var nombre by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var mensajeRegistro by remember { mutableStateOf("") }

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Registro de Nuevo Socio", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))

            // Campo de Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña (PIN)") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Botón de Registro
            Button(
                modifier = Modifier.fillMaxWidth().height(50.dp),
                onClick = {
                    if (nombre.isNotBlank() && password.isNotBlank()) {
                        // 1. GENERAR EL TOKEN AUTOMÁTICO
                        // Toma las iniciales del nombre y le pega 4 números al azar
                        val iniciales = nombre.take(2).uppercase()
                        val numeroAzar = (1000..9999).random()
                        val tokenGenerado = "QR_${iniciales}_${numeroAzar}"

                        // 2. GUARDAR EN LA BASE DE DATOS
                        try {
                            database.gymDatabaseQueries.insertarUsuario(
                                nombre = nombre,
                                password = password,
                                qr_token = tokenGenerado,
                                id_rol = 2,
                                id_estado = 1 // 1 para cuenta activa
                            )
                            mensajeRegistro = "✅ Registro exitoso.\nTu Token es: $tokenGenerado\n¡Tómale captura!"

                            // Limpiamos los campos para el siguiente registro
                            nombre = ""
                            password = ""
                        } catch (e: Exception) {
                            mensajeRegistro = "❌ Error al guardar usuario"
                        }
                    } else {
                        mensajeRegistro = "⚠️ Llena todos los campos"
                    }
                }
            ) {
                Text("Registrar")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Aquí mostramos el token generado para que el usuario pueda ir a crear su código QR en la web
            if (mensajeRegistro.isNotEmpty()) {
                Text(
                    text = mensajeRegistro,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón para volver atrás
            TextButton(onClick = { navigator.pop() }) {
                Text("Volver al Login")
            }
        }
    }
}