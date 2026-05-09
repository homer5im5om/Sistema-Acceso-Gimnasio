package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.example.project.data.GymDatabase
import java.io.File // Don't forget this import!

fun main() = application {
    // 1. Check if the database file physically exists BEFORE connecting
    val dbFile = File("gym.db")
    val isNewDatabase = !dbFile.exists()

    // 2. Connect the driver
    val driver = JdbcSqliteDriver("jdbc:sqlite:gym.db")

    // 3. ONLY create the schema (tables) if it's the very first time
    if (isNewDatabase) {
        GymDatabase.Schema.create(driver)
    }

    // Initialize the database
    val database = GymDatabase(driver)

    // Check if the "admin" user exists, if not, create it
    val adminExists = database.gymDatabaseQueries.verificarLogin("admin", "123").executeAsList()
    if (adminExists.isEmpty()) {
        database.gymDatabaseQueries.insertarUsuario(
            nombre = "admin",
            id_rol = 1,
            qr_token = "token_maestro",
            password = "123"
        )
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Acceso Gimnasio",
    ) {
        App(database = database)
    }
}