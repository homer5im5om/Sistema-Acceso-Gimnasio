package org.example.project.presentation.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.project.data.GymDatabase
import org.example.project.obtenerFechaHoraActual // Tu función expect/actual de la fecha

// 1. UIState: Empaqueta el mensaje y si fue un acceso exitoso o denegado
data class EscanerUiState(
    val mensajeResultado: String = "",
    val accesoPermitido: Boolean = false,
    val mostrarResultado: Boolean = false
)

class EscanerViewModel(private val database: GymDatabase) {

    private val _uiState = MutableStateFlow(EscanerUiState())
    val uiState: StateFlow<EscanerUiState> = _uiState.asStateFlow()

    // 2. LÓGICA DE NEGOCIO Y TRANSACCIÓN OBLIGATORIA
    fun procesarCodigoQR(qrToken: String) {
        if (qrToken.isBlank()) return

        var mensaje = ""
        var permitido = false

        // REQUISITO DE RÚBRICA: Uso explícito de transacciones
        database.gymDatabaseQueries.transaction {
            // Buscamos al usuario por su token
            val usuario = database.gymDatabaseQueries.obtenerUsuarioPorToken(qrToken).executeAsOneOrNull()
            val fechaHoraActual = obtenerFechaHoraActual()

            if (usuario != null) {
                // Verificamos su estado (1 = Activo)
                if (usuario.id_estado == 1L) {
                    mensaje = "✅ Acceso Concedido: ${usuario.nombre}"
                    permitido = true
                } else {
                    val estadoStr = if (usuario.id_estado == 2L) "Vencida" else "Suspendida"
                    mensaje = "❌ Acceso Denegado: ${usuario.nombre}\n(Membresía $estadoStr)"
                    permitido = false
                }

                // Generamos el registro en la bitácora (Requisito: Registro de acceso que genera bitácora)
                database.gymDatabaseQueries.insertarAcceso(
                    id_usuario = usuario.id_usuario,
                    fecha_hora = fechaHoraActual,
                    acceso_permitido = if (permitido) 1L else 0L
                )
            } else {
                mensaje = "❌ Código no reconocido o inválido"
                permitido = false
            }
        }

        // Actualizamos la pantalla con el resultado final
        _uiState.value = EscanerUiState(
            mensajeResultado = mensaje,
            accesoPermitido = permitido,
            mostrarResultado = true
        )
    }

    fun limpiarResultado() {
        _uiState.value = EscanerUiState()
    }
}