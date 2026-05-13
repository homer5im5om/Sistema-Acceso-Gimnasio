package org.example.project

import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileWriter

actual fun exportarAExcel(contenidoCsv: String, nombreArchivo: String): String {
    // Usamos el contexto global de la app para acceder al sistema de archivos
    val context = MainActivity.appContext ?: return "❌ Error: Contexto no inicializado"

    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // ANDROID 10 O SUPERIOR (Usa MediaStore)
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$nombreArchivo.csv")
                put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(contenidoCsv.toByteArray())
                }
                "✅ Guardado en Descargas: $nombreArchivo.csv"
            } else {
                "❌ Error al crear el archivo en Descargas"
            }
        } else {
            // ANDROID 9 O INFERIOR (Usa el método clásico)
            val carpetaDescargas = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val archivo = File(carpetaDescargas, "$nombreArchivo.csv")

            val writer = FileWriter(archivo)
            writer.append(contenidoCsv)
            writer.flush()
            writer.close()

            "✅ Guardado en Descargas: $nombreArchivo.csv"
        }
    } catch (e: Exception) {
        "❌ Error al guardar: ${e.message}"
    }
}