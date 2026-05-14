package org.example.project.presentation.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.project.data.GymDatabase
import org.example.project.data.Usuario

// 1. Nuestro UIState ahora empaqueta la lista completa y a quién queremos borrar
data class ListaSociosUiState(
    val usuarios: List<Usuario> = emptyList(),
    val usuarioAEliminar: Usuario? = null
)

class ListaSociosViewModel(private val database: GymDatabase) {

    private val _uiState = MutableStateFlow(ListaSociosUiState())
    val uiState: StateFlow<ListaSociosUiState> = _uiState.asStateFlow()

    init {
        // Al nacer el ViewModel, cargamos la lista automáticamente
        cargarUsuarios()
    }

    fun cargarUsuarios() {
        val lista = database.gymDatabaseQueries.obtenerTodosLosUsuarios().executeAsList()
        _uiState.value = _uiState.value.copy(usuarios = lista)
    }

    // Funciones para controlar la ventana de confirmación
    fun prepararEliminacion(usuario: Usuario) {
        _uiState.value = _uiState.value.copy(usuarioAEliminar = usuario)
    }

    fun cancelarEliminacion() {
        _uiState.value = _uiState.value.copy(usuarioAEliminar = null)
    }

    fun confirmarEliminacion() {
        val usuario = _uiState.value.usuarioAEliminar
        if (usuario != null) {
            // Mantenemos la transacción segura aquí en la capa lógica
            database.gymDatabaseQueries.transaction {
                database.gymDatabaseQueries.eliminarHistorialUsuario(usuario.id_usuario)
                database.gymDatabaseQueries.eliminarUsuario(usuario.id_usuario)
            }
            // Ocultamos la ventana y recargamos la lista actualizada
            _uiState.value = _uiState.value.copy(usuarioAEliminar = null)
            cargarUsuarios()
        }
    }
}