package org.example.project.presentation.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.project.data.GymDatabase
import org.example.project.data.Usuario

data class ListaSociosUiState(
    val usuarios: List<Usuario> = emptyList(),
    val usuarioAEliminar: Usuario? = null,
    // NEW: We add the search text to the UI State
    val textoBusqueda: String = ""
)

class ListaSociosViewModel(private val database: GymDatabase) {

    private val _uiState = MutableStateFlow(ListaSociosUiState())
    val uiState: StateFlow<ListaSociosUiState> = _uiState.asStateFlow()

    // We keep the master list in memory to filter quickly
    private var listaCompleta: List<Usuario> = emptyList()

    init {
        cargarUsuarios()
    }

    fun cargarUsuarios() {
        listaCompleta = database.gymDatabaseQueries.obtenerTodosLosUsuarios().executeAsList()
        actualizarBusqueda(_uiState.value.textoBusqueda) // Re-apply filter if data changes
    }

    // NEW: This function filters the list every time the user types a letter
    fun actualizarBusqueda(texto: String) {
        val listaFiltrada = if (texto.isBlank()) {
            listaCompleta
        } else {
            listaCompleta.filter {
                it.nombre.contains(texto, ignoreCase = true) ||
                        it.qr_token.contains(texto, ignoreCase = true)
            }
        }

        _uiState.value = _uiState.value.copy(
            textoBusqueda = texto,
            usuarios = listaFiltrada
        )
    }

    fun prepararEliminacion(usuario: Usuario) {
        _uiState.value = _uiState.value.copy(usuarioAEliminar = usuario)
    }

    fun cancelarEliminacion() {
        _uiState.value = _uiState.value.copy(usuarioAEliminar = null)
    }

    fun confirmarEliminacion() {
        val usuario = _uiState.value.usuarioAEliminar
        if (usuario != null) {
            database.gymDatabaseQueries.transaction {
                database.gymDatabaseQueries.eliminarHistorialUsuario(usuario.id_usuario)
                database.gymDatabaseQueries.eliminarUsuario(usuario.id_usuario)
            }
            _uiState.value = _uiState.value.copy(usuarioAEliminar = null)
            cargarUsuarios()
        }
    }
}