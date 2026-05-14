package org.example.project.presentation.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.project.data.GymDatabase
import org.example.project.data.Usuario

// 1. REQUISITO DE RÚBRICA: Uso de UIState con data class
data class LoginUiState(
    val usuarioLogueado: Usuario? = null,
    val mensajeError: String = ""
)

// 2. El ViewModel se encarga de la lógica, no de dibujar pantallas
class LoginViewModel(private val database: GymDatabase) {

    // Aquí guardamos el estado actual y lo hacemos reactivo (Flow)
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun verificarLogin(usuario: String, password: String) {
        // La consulta SQL se muda para acá, la UI ya no la toca
        val usuarioEncontrado = database.gymDatabaseQueries.verificarLogin(usuario, password).executeAsOneOrNull()

        if (usuarioEncontrado != null) {
            // Si el login es exitoso, actualizamos el estado con el usuario
            _uiState.value = LoginUiState(usuarioLogueado = usuarioEncontrado, mensajeError = "")
        } else {
            // Si falla, actualizamos el estado con el error
            _uiState.value = LoginUiState(usuarioLogueado = null, mensajeError = "Credenciales incorrectas")
        }
    }

    fun limpiarError() {
        _uiState.value = _uiState.value.copy(mensajeError = "")
    }
}