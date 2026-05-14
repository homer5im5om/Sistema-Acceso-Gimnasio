package org.example.project.presentation.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.project.data.GymDatabase

// 1. UIState: Controla si se actualizó correctamente o si hubo un error
data class EditarSocioUiState(
    val actualizacionExitosa: Boolean = false,
    val mensajeError: String = ""
)

class EditarSocioViewModel(private val database: GymDatabase) {

    private val _uiState = MutableStateFlow(EditarSocioUiState())
    val uiState: StateFlow<EditarSocioUiState> = _uiState.asStateFlow()

    fun actualizarUsuario(idUsuario: Long, nombre: String, password: String, idEstado: Long) {
        if (nombre.isBlank() || password.isBlank()) {
            _uiState.value = EditarSocioUiState(mensajeError = "El nombre y la contraseña no pueden estar vacíos.")
            return
        }

        try {
            // Mandamos llamar el UPDATE de tu base de datos
            database.gymDatabaseQueries.actualizarUsuario(
                nombre = nombre,
                password = password,
                id_estado = idEstado,
                id_usuario = idUsuario
            )

            // Si todo sale bien, avisamos a la pantalla para que regrese
            _uiState.value = EditarSocioUiState(actualizacionExitosa = true, mensajeError = "")

        } catch (e: Exception) {
            _uiState.value = EditarSocioUiState(
                actualizacionExitosa = false,
                mensajeError = "Error al actualizar: ${e.message}"
            )
        }
    }

    fun limpiarError() {
        _uiState.value = _uiState.value.copy(mensajeError = "")
    }
}