package org.example.project.presentation.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.project.data.GymDatabase
import org.example.project.exportarAExcel
// Este es el tipo de dato que genera SQLDelight para tu consulta.
// Si sale en rojo, presiona Alt + Enter -> Import
import org.example.project.data.ObtenerHistorialAccesos

// 1. UIState: Empaqueta la lista del historial y los mensajes de éxito/error al exportar
data class BitacoraUiState(
    val historial: List<ObtenerHistorialAccesos> = emptyList(),
    val mensajeExportacion: String = ""
)

class BitacoraViewModel(private val database: GymDatabase) {

    private val _uiState = MutableStateFlow(BitacoraUiState())
    val uiState: StateFlow<BitacoraUiState> = _uiState.asStateFlow()

    init {
        // Al entrar a la pantalla, cargamos el historial de la base de datos
        cargarHistorial()
    }

    private fun cargarHistorial() {
        val lista = database.gymDatabaseQueries.obtenerHistorialAccesos().executeAsList()
        _uiState.value = _uiState.value.copy(historial = lista)
    }

    // 2. LÓGICA DE NEGOCIO: Armar el Excel ya no es trabajo de la pantalla
    fun exportarHistorial() {
        val registros = _uiState.value.historial

        if (registros.isEmpty()) {
            _uiState.value = _uiState.value.copy(mensajeExportacion = "⚠️ No hay registros para exportar.")
            return
        }

        // Armamos el texto del CSV
        val encabezados = "ID Bitacora,Nombre del Socio,Fecha y Hora,Estado del Acceso\n"
        val filas = registros.joinToString("\n") { registro ->
            val estado = if (registro.acceso_permitido == 1L) "Concedido" else "Denegado"
            "${registro.id_bitacora},${registro.nombre},${registro.fecha_hora},$estado"
        }
        val csvCompleto = "\uFEFF" + encabezados + filas

        // Llamamos a nuestra función multiplataforma
        val resultado = exportarAExcel(csvCompleto, "Reporte_Accesos_Gym")

        // Actualizamos el estado para que la pantalla muestre el mensaje
        _uiState.value = _uiState.value.copy(mensajeExportacion = resultado)
    }
}