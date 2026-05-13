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
import org.example.project.data.Usuario

class EditarSocioScreen(val database: GymDatabase, val usuarioAEditar: Usuario) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        var nombre by remember { mutableStateOf(usuarioAEditar.nombre) }
        var password by remember { mutableStateOf(usuarioAEditar.password) }
        var estadoSeleccionado by remember { mutableStateOf(usuarioAEditar.id_estado) }
        var expandido by remember { mutableStateOf(false) }

        val opcionesEstado = mapOf(1L to "Activo", 2L to "Vencido", 3L to "Suspendido")

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Editar Socio", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña (PIN)") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Selector de Estado de Membresía
            ExposedDropdownMenuBox(
                expanded = expandido,
                onExpandedChange = { expandido = !expandido }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = opcionesEstado[estadoSeleccionado] ?: "Desconocido",
                    onValueChange = { },
                    label = { Text("Estado de Membresía") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandido,
                    onDismissRequest = { expandido = false }
                ) {
                    opcionesEstado.forEach { (id, descripcion) ->
                        DropdownMenuItem(
                            text = { Text(descripcion) },
                            onClick = {
                                estadoSeleccionado = id
                                expandido = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                modifier = Modifier.fillMaxWidth().height(50.dp),
                onClick = {
                    if (nombre.isNotBlank() && password.isNotBlank()) {
                        // Usamos la consulta actualizarUsuario que agregamos anteriormente
                        database.gymDatabaseQueries.actualizarUsuario(
                            nombre = nombre,
                            password = password,
                            id_estado = estadoSeleccionado,
                            id_usuario = usuarioAEditar.id_usuario
                        )
                        navigator.pop() // Regresamos a la lista después de guardar
                    }
                }
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