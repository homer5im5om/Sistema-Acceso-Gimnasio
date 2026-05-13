package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.example.project.data.GymDatabase
import java.io.File

fun main() = application {
    // 1. Cambia el nombre en el File
    val dbFile = File("gym_v2.db")
    val isNewDatabase = !dbFile.exists()

// 2. Cambia el nombre en la conexión
    val driver = JdbcSqliteDriver("jdbc:sqlite:gym_v2.db")

    if (isNewDatabase) {
        GymDatabase.Schema.create(driver)
    }

    val database = GymDatabase(driver)

    // 1. LLENAR CATÁLOGOS SI ESTÁN VACÍOS
    if (database.gymDatabaseQueries.contarRoles().executeAsOne() == 0L) {
        database.gymDatabaseQueries.transaction {
            database.gymDatabaseQueries.insertarRol(1, "Administrador")
            database.gymDatabaseQueries.insertarRol(2, "Socio")

            database.gymDatabaseQueries.insertarEstado(1, "Activo")
            database.gymDatabaseQueries.insertarEstado(2, "Vencido")
            database.gymDatabaseQueries.insertarEstado(3, "Suspendido")
        }
    }

    // 2. CREAR ADMIN SI NO EXISTE
    val adminExists = database.gymDatabaseQueries.verificarLogin("admin", "123").executeAsList()
    if (adminExists.isEmpty()) {
        database.gymDatabaseQueries.insertarUsuario(
            nombre = "admin",
            password = "123",
            qr_token = "token_maestro",
            id_rol = 1,
            id_estado = 1
        )
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Acceso Gimnasio",
    ) {
        App(database = database)
    }
}