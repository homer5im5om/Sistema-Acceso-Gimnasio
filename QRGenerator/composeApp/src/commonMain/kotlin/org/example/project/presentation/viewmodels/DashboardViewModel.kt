package org.example.project.presentation.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.project.data.GymDatabase
import org.example.project.data.ObtenerEstadisticasEstados

data class DashboardUiState(
    val estadisticas: List<ObtenerEstadisticasEstados> = emptyList(),
    val totalSocios: Long = 0,
    val maxValor: Long = 1,
    val afluenciaPorHora: List<Pair<Float, Float>> = emptyList(),
    val topSociosBarras: List<Pair<String, Float>> = emptyList()
)

class DashboardViewModel(private val database: GymDatabase) {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun cargarDatosDashboard() {
        val stats = database.gymDatabaseQueries.obtenerEstadisticasEstados().executeAsList()
        val total = database.gymDatabaseQueries.contarSociosTotales().executeAsOneOrNull() ?: 0L
        val max = stats.maxOfOrNull { it.total } ?: 1

        // ==========================================
        // 2. CÁLCULO DE AFLUENCIA POR HORA
        // ==========================================
        val historialReal = database.gymDatabaseQueries.obtenerHistorialAccesos().executeAsList()
        val afluenciaMap = mutableMapOf<Int, Int>()

        for (hora in 6..22) {
            afluenciaMap[hora] = 0
        }

        historialReal.forEach { registro ->
            val horaExtraida = extraerHoraSegura(registro.fecha_hora)
            if (horaExtraida != null && horaExtraida in 6..22) {
                afluenciaMap[horaExtraida] = afluenciaMap.getOrDefault(horaExtraida, 0) + 1
            }
        }

        val afluenciaReal = afluenciaMap.map { it.key.toFloat() to it.value.toFloat() }.sortedBy { it.first }

        // ==========================================
        // 3. CÁLCULO TOP SOCIOS
        // ==========================================
        val topSociosDb = database.gymDatabaseQueries.obtenerTopSocios().executeAsList()
        val topSociosReal = topSociosDb.map {
            it.nombre to it.total_asistencias.toFloat()
        }

        _uiState.value = DashboardUiState(
            estadisticas = stats,
            totalSocios = total,
            maxValor = max,
            afluenciaPorHora = afluenciaReal,
            topSociosBarras = topSociosReal
        )
    }

    // ==========================================
    // AM/PM a 24 HRS)
    // ==========================================
    private fun extraerHoraSegura(fechaHora: String): Int? {
        return try {
            val partes = fechaHora.replace("T", " ").split(" ")
            if (partes.size >= 2) {
                val tiempo = partes[1] // Agarra "04:20"
                var hora = tiempo.split(":")[0].toInt() // Agarra el 4

                if (partes.size >= 3) {
                    val amPm = partes[2].uppercase()
                    if (amPm == "PM" && hora < 12) {
                        hora += 12
                    } else if (amPm == "AM" && hora == 12) {
                        hora = 0
                    }
                }
                hora
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}