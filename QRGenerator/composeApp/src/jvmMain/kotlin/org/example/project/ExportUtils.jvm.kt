package org.example.project

import java.io.File
import java.io.FileWriter

actual fun exportarAExcel(contenidoCsv: String, nombreArchivo: String): String {
    return try {
        // Buscamos la carpeta de Descargas (Downloads) de la computadora
        val home = System.getProperty("user.home")
        val archivo = File(home, "Downloads/$nombreArchivo.csv")

        val writer = FileWriter(archivo)
        writer.append(contenidoCsv)
        writer.flush()
        writer.close()

        "✅ Guardado en Descargas: $nombreArchivo.csv"
    } catch (e: Exception) {
        "❌ Error al guardar: ${e.message}"
    }
}