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

class ListaSociosScreen(val database: GymDatabase) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        // Cambiamos a 'var' para poder recargar la lista visualmente cuando borremos a alguien
        var listaUsuarios by remember { mutableStateOf(database.gymDatabaseQueries.obtenerTodosLosUsuarios().executeAsList()) }

        // Estado para controlar si mostramos la ventanita de confirmación y a quién vamos a borrar
        var usuarioAEliminar by remember { mutableStateOf<Usuario?>(null) }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text("Lista de Socios", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // Dentro de Content() en ListaSociosScreen
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(listaUsuarios) { usuario ->
                    TarjetaUsuario(
                        usuario = usuario,
                        onEliminarClick = { usuarioAEliminar = usuario },
                        onEditarClick = { navigator.push(EditarSocioScreen(database, usuario)) } // Navegamos a Editar
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
        // REQUISITO: Confirmación de eliminación
        // ==========================================
        usuarioAEliminar?.let { usuario ->
            AlertDialog(
                onDismissRequest = { usuarioAEliminar = null }, // Si tocan fuera de la ventana, se cancela
                title = { Text("Confirmar Eliminación") },
                text = { Text("¿Estás seguro de que deseas eliminar a ${usuario.nombre}? Esta acción también borrará su historial de accesos y no se puede deshacer.") },
                confirmButton = {
                    Button(
                        onClick = {
                            // Usamos transacción porque afectamos dos tablas
                            database.gymDatabaseQueries.transaction {
                                database.gymDatabaseQueries.eliminarHistorialUsuario(usuario.id_usuario)
                                database.gymDatabaseQueries.eliminarUsuario(usuario.id_usuario)
                            }
                            // Ocultamos la ventana y recargamos la lista
                            usuarioAEliminar = null
                            listaUsuarios = database.gymDatabaseQueries.obtenerTodosLosUsuarios().executeAsList()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { usuarioAEliminar = null }) {
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

                // Mostramos el estado actual para que sea visible
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
                // Protegemos al admin principal para no editarlo ni borrarlo accidentalmente
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