package org.example.project.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import org.example.project.presentation.viewmodels.ListaSociosViewModel // Importamos el ViewModel

class ListaSociosScreen(val database: GymDatabase) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        // 1. Instanciamos el ViewModel
        val viewModel = remember { ListaSociosViewModel(database) }
        val uiState by viewModel.uiState.collectAsState()

        // 2. Efecto para recargar la lista siempre que entremos a esta pantalla
        // (Muy útil por si venimos de editar a un usuario)
        LaunchedEffect(Unit) {
            viewModel.cargarUsuarios()
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text("Lista de Socios", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(uiState.usuarios) { usuario ->
                    TarjetaUsuario(
                        usuario = usuario,
                        // Le pasamos las acciones al ViewModel en lugar de manejar la base de datos aquí
                        onEliminarClick = { viewModel.prepararEliminacion(usuario) },
                        onEditarClick = { navigator.push(EditarSocioScreen(database, usuario)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navigator.pop() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Regresar al Menú")
            }
        }

        // ==========================================
        // Ventana de Confirmación controlada por el UIState
        // ==========================================
        uiState.usuarioAEliminar?.let { usuario ->
            AlertDialog(
                onDismissRequest = { viewModel.cancelarEliminacion() },
                title = { Text("Confirmar Eliminación") },
                text = { Text("¿Estás seguro de que deseas eliminar a ${usuario.nombre}? Esta acción también borrará su historial de accesos y no se puede deshacer.") },
                confirmButton = {
                    Button(
                        onClick = { viewModel.confirmarEliminacion() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.cancelarEliminacion() }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun TarjetaUsuario(usuario: Usuario, onEliminarClick: () -> Unit, onEditarClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Nombre: ${usuario.nombre}", style = MaterialTheme.typography.titleMedium)
                Text(text = "Rol: ${if (usuario.id_rol == 1L) "Administrador" else "Socio"}", style = MaterialTheme.typography.bodyMedium)

                val textoEstado = when(usuario.id_estado) {
                    1L -> "Activo"
                    2L -> "Vencido"
                    3L -> "Suspendido"
                    else -> "Desconocido"
                }
                Text(text = "Estado: $textoEstado", style = MaterialTheme.typography.bodySmall)
                Text(text = "Token: ${usuario.qr_token}", style = MaterialTheme.typography.bodySmall)
            }

            Row {
                if (usuario.id_rol != 1L) {
                    IconButton(onClick = onEditarClick) {
                        Text("✏️", style = MaterialTheme.typography.headlineSmall)
                    }
                    IconButton(onClick = onEliminarClick) {
                        Text("🗑️", style = MaterialTheme.typography.headlineSmall)
                    }
                }
            }
        }
    }
}