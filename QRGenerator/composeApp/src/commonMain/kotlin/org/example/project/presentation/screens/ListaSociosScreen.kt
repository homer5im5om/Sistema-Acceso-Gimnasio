package org.example.project.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

        // Obtenemos la lista de usuarios desde la base de datos
        val listaUsuarios = remember { database.gymDatabaseQueries.obtenerTodosLosUsuarios().executeAsList() }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text("Lista de Socios", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // Usamos LazyColumn para crear una lista scrolleable
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(listaUsuarios) { usuario ->
                    TarjetaUsuario(usuario)
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
    }
}

// Un componente para mostrar los datos de cada usuario de forma elegante
@Composable
fun TarjetaUsuario(usuario: Usuario) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Nombre: ${usuario.nombre}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Rol: ${if (usuario.id_rol == 1L) "Administrador" else "Socio"}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Token: ${usuario.qr_token}", style = MaterialTheme.typography.bodySmall)
        }
    }
}