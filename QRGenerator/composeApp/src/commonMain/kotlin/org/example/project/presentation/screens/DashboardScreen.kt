package org.example.project.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.data.GymDatabase
import org.example.project.data.Usuario
import androidx.compose.foundation.Image
import qrgenerator.qrkitpainter.rememberQrKitPainter
import org.example.project.presentation.viewmodels.DashboardViewModel
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin

class DashboardScreen(val database: GymDatabase, val usuarioLogueado: Usuario) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val dashboardVm = remember { DashboardViewModel(database) }
        val statsState by dashboardVm.uiState.collectAsState()

        // Estado para permitir que la pantalla haga scroll hacia abajo
        val scrollState = rememberScrollState()

        // carga de datos solo si es Administrador
        LaunchedEffect(Unit) {
            if (usuarioLogueado.id_rol == 1L) {
                dashboardVm.cargarDatosDashboard()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text("¡Bienvenido, ${usuarioLogueado.nombre}!", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(24.dp))

            if (usuarioLogueado.id_rol == 1L) {
                // ====================================================
                // VISTA DEL ADMINISTRADOR
                // ====================================================
                Text("Panel de Control Estratégico", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { navigator.push(EscanerScreen(database)) }, modifier = Modifier.weight(1f)) {
                        Text("📷 Escáner", fontSize = 12.sp)
                    }
                    Button(onClick = { navigator.push(ListaSociosScreen(database)) }, modifier = Modifier.weight(1f)) {
                        Text("👥 Socios", fontSize = 12.sp)
                    }
                    Button(onClick = { navigator.push(BitacoraScreen(database)) }, modifier = Modifier.weight(1f)) {
                        Text("📜 Bitácora", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                //MÉTRICAS RÁPIDAS
                // SECCIÓN DE MÉTRICAS RÁPIDAS
                val sociosActivos = statsState.estadisticas.find { it.id_estado == 1L }?.total ?: 0L
                val porcentajeRetencion = if (statsState.totalSocios > 0) {
                    ((sociosActivos.toFloat() / statsState.totalSocios.toFloat()) * 100).toInt()
                } else {
                    0
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MetricaCard("Socios Totales", "${statsState.totalSocios}", Modifier.weight(1f))
                    MetricaCard("Retención", "$porcentajeRetencion%", Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ESTADO DE MEMBRESÍAS
                Text("Estado Actual de Membresías", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        statsState.estadisticas.forEach { stat ->
                            val (label, color) = when(stat.id_estado) {
                                1L -> "Activos" to Color(0xFF4CAF50)
                                2L -> "Vencidos" to Color(0xFFF44336)
                                else -> "Suspendidos" to Color(0xFFFF9800)
                            }
                            val porcentaje = if (statsState.maxValor > 0) stat.total.toFloat() / statsState.maxValor.toFloat() else 0f

                            Column {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(label, style = MaterialTheme.typography.bodySmall)
                                    Text("${stat.total}", fontWeight = FontWeight.Bold)
                                }
                                Box(modifier = Modifier.fillMaxWidth().height(12.dp).background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(4.dp))) {
                                    Box(modifier = Modifier.fillMaxWidth(porcentaje).fillMaxHeight().background(color, RoundedCornerShape(4.dp)))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // TOP 4 SOCIOS  ---
                Text("Top 4 Socios (Asistencias Totales)", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        val maxCrecimiento = statsState.topSociosBarras.maxOfOrNull { it.second } ?: 1f

                        if (statsState.topSociosBarras.isEmpty()) {
                            Text("Aún no hay asistencias registradas.", color = Color.Gray, fontSize = 12.sp)
                        } else {
                            statsState.topSociosBarras.forEach { (nombreSocio, asistencias) ->
                                val proporcion = if (maxCrecimiento > 0) asistencias / maxCrecimiento else 0f

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(nombreSocio, color = Color.White, modifier = Modifier.width(80.dp), fontSize = 12.sp, maxLines = 1)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(24.dp)
                                            .background(Color.DarkGray, RoundedCornerShape(4.dp))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(proporcion)
                                                .fillMaxHeight()
                                                .background(Color(0xFFDEFF9A), RoundedCornerShape(4.dp)) // Color verde neón
                                        ) {
                                            Text("${asistencias.toInt()}", color = Color.Black, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp), fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // AFLUENCIA POR HORA (Nativo)
                Text("Afluencia por Franja Horaria", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                ) {
                    Box(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                        if (statsState.afluenciaPorHora.isNotEmpty() && statsState.afluenciaPorHora.any { it.second > 0 }) {

                            val maxAccesos = statsState.afluenciaPorHora.maxOfOrNull { it.second } ?: 1f
                            val techoGrafica = if (maxAccesos < 5f) 5f else maxAccesos + (maxAccesos * 0.2f)

                            Row(modifier = Modifier.fillMaxSize()) {
                                // --- EJE Y
                                Column(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .padding(bottom = 20.dp, end = 8.dp),
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text("${techoGrafica.toInt()}", color = Color.Gray, fontSize = 10.sp)
                                    Text("${(techoGrafica / 2).toInt()}", color = Color.Gray, fontSize = 10.sp)
                                    Text("0", color = Color.Gray, fontSize = 10.sp)
                                }

                                // ÁREA
                                Column(modifier = Modifier.fillMaxSize()) {

                                    // 1. El canvas
                                    Canvas(modifier = Modifier.fillMaxWidth().weight(1f)) {
                                        val minHora = 6f
                                        val maxHora = 22f
                                        val rangoHoras = maxHora - minHora

                                        val ancho = size.width
                                        val alto = size.height
                                        val path = Path()

                                        drawLine(color = Color.DarkGray.copy(alpha = 0.5f), start = androidx.compose.ui.geometry.Offset(0f, 0f), end = androidx.compose.ui.geometry.Offset(ancho, 0f), strokeWidth = 2f)
                                        drawLine(color = Color.DarkGray.copy(alpha = 0.5f), start = androidx.compose.ui.geometry.Offset(0f, alto / 2), end = androidx.compose.ui.geometry.Offset(ancho, alto / 2), strokeWidth = 2f)
                                        drawLine(color = Color.DarkGray.copy(alpha = 0.5f), start = androidx.compose.ui.geometry.Offset(0f, alto), end = androidx.compose.ui.geometry.Offset(ancho, alto), strokeWidth = 2f)

                                        statsState.afluenciaPorHora.forEachIndexed { index, punto ->
                                            val hora = punto.first
                                            val accesos = punto.second

                                            val x = ((hora - minHora) / rangoHoras) * ancho
                                            val y = alto - ((accesos / techoGrafica) * alto)

                                            if (index == 0) {
                                                path.moveTo(x, y)
                                            } else {
                                                path.lineTo(x, y)
                                            }

                                            drawCircle(
                                                color = Color.White,
                                                radius = 6f,
                                                center = androidx.compose.ui.geometry.Offset(x, y)
                                            )
                                        }

                                        drawPath(
                                            path = path,
                                            color = Color(0xFFDEFF9A),
                                            style = Stroke(
                                                width = 8f,
                                                cap = StrokeCap.Round,
                                                join = StrokeJoin.Round
                                            )
                                        )
                                    }

                                    // EJE X (Las Horas espaciadas)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("6 AM", color = Color.Gray, fontSize = 10.sp)
                                        Text("10 AM", color = Color.Gray, fontSize = 10.sp)
                                        Text("2 PM", color = Color.Gray, fontSize = 10.sp)
                                        Text("6 PM", color = Color.Gray, fontSize = 10.sp)
                                        Text("10 PM", color = Color.Gray, fontSize = 10.sp)
                                    }
                                }
                            }

                        } else {
                            Text("Aún no hay datos de afluencia suficientes.", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }

            } else {
                // ====================================================
                // VISTA DEL SOCIO
                // ====================================================
                Text("Tu Pase de Acceso", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(16.dp))

                Card(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        val qrPainter = rememberQrKitPainter(data = usuarioLogueado.qr_token)
                        Image(painter = qrPainter, contentDescription = null, modifier = Modifier.size(200.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(usuarioLogueado.qr_token, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(onClick = { navigator.popAll() }) {
                Text("Cerrar Sesión")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// Componente para pintar de color negro/neón
@Composable
fun MetricaCard(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(valor, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDEFF9A))
            Spacer(modifier = Modifier.height(8.dp))
            Text(titulo, fontSize = 12.sp, color = Color.Gray)
        }
    }
}