package org.example.project

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// We provide the "actual" implementation for Android
actual fun obtenerFechaHoraActual(): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
    return formatter.format(Date())
}