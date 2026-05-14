package org.example.project.presentation.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.project.data.GymDatabase
import kotlin.random.Random

// 1. UIState: Keeps track of errors and whether the registration was successful
data class RegistroUiState(
    val registroExitoso: Boolean = false,
    val mensajeError: String = ""
)

class RegistroViewModel(private val database: GymDatabase) {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun registrarUsuario(nombre: String, password: String) {
        if (nombre.isBlank() || password.isBlank()) {
            _uiState.value = RegistroUiState(mensajeError = "Por favor, llena todos los campos.")
            return
        }

        try {
            // Generate a unique simulated QR token (e.g., QR_EDU_4059)
            val prefijo = nombre.take(3).uppercase().padEnd(3, 'X')
            val numeroRandom = Random.nextInt(1000, 9999)
            val qrToken = "QR_${prefijo}_${numeroRandom}"

            // Hardcode standard member settings: Rol 2 (Socio), Estado 1 (Activo)
            val idRol = 2L
            val idEstado = 1L

            // Insert into the database
            database.gymDatabaseQueries.insertarUsuario(
                nombre = nombre,
                password = password,
                qr_token = qrToken,
                id_rol = idRol,
                id_estado = idEstado
            )

            // Trigger the success state
            _uiState.value = RegistroUiState(registroExitoso = true, mensajeError = "")

        } catch (e: Exception) {
            _uiState.value = RegistroUiState(
                registroExitoso = false,
                mensajeError = "Error al registrar la cuenta: ${e.message}"
            )
        }
    }

    fun limpiarError() {
        _uiState.value = _uiState.value.copy(mensajeError = "")
    }
}