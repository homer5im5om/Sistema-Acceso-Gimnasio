package org.example.project

// Esperamos que cada plataforma sepa cómo guardar un archivo de texto/csv
expect fun exportarAExcel(contenidoCsv: String, nombreArchivo: String): String